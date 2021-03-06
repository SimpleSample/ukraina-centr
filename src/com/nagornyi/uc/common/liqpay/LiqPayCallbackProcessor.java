package com.nagornyi.uc.common.liqpay;

import com.nagornyi.uc.common.mail.MailFacade;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IOrderDAO;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Nagornyi
 * Date: 17.06.14
 */
public class LiqPayCallbackProcessor extends HttpServlet {
    private static Logger log = Logger.getLogger(LiqPayCallbackProcessor.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        log.info("received info from liqpay");
        Enumeration names = req.getParameterNames();
        Map<String, String> liqPayParams = new HashMap<String, String>();

        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            liqPayParams.put(name, req.getParameter(name));
        }
        log.info("liqpay params: " + liqPayParams.toString());
        if (!LiqPay.isValid(liqPayParams)) {
            log.warning("Signature validation failed");
        } else {
            liqPayParams = LiqPay.parseParams(liqPayParams);
            String status = liqPayParams.get("status");
            String order_id_desc = liqPayParams.get("order_id");
            String transactionId = liqPayParams.get("transaction_id");
            String orderExternalId = order_id_desc.substring(0, order_id_desc.indexOf(LiqPay.UC_KEY));
            log.info("order_id: " + orderExternalId);
            IOrderDAO dao = DAOFacade.getDAO(Order.class);
            Order order = dao.findByExternalId(Long.valueOf(orderExternalId));
            List<Ticket> tickets = order.getTickets();
            User user = order.getUser();
            order.setTransactionId(transactionId);

            if (LiqPayStatus.isOneOfFailed(status)) {
                order.failed();
                MailFacade.sendFailedTicketsPurchaseFromLiqPay(user, transactionId);
            } else if (LiqPayStatus.isOneOfProgress(status)) {
                order.processing();
            } else {
                order.succeeded();
                MailFacade.sendSuccessfulReservation(user, tickets);
            }
        }
    }
}
