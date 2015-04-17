package com.nagornyi.uc.common.liqpay;

import com.google.appengine.api.datastore.KeyFactory;
import com.nagornyi.uc.common.mail.MailFacade;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("received info from liqpay");
        Enumeration names = req.getParameterNames();
        Map<String, String> liqPayParams = new HashMap<String, String>();

        while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            liqPayParams.put(name, req.getParameter(name));
        }

        if (!LiqPay.isValid(liqPayParams)) {
            log.warning("Signature validation failed, liqpay params: " + liqPayParams.toString());
        } else {
            String status = liqPayParams.get("status");
            Order order = DAOFacade.findById(Order.class, KeyFactory.stringToKey(liqPayParams.get("order_id")));
            List<Ticket> tickets = order.getTickets();
            User user = order.getUser();
            order.setTransactionId(liqPayParams.get("transaction_id"));

            if ("success".equals(status) || "sandbox".equals(status)) {
                order.setStatus(Order.Status.SUCCESS);
                for (Ticket ticket: tickets) {
                    ticket.setStatus(Ticket.Status.RESERVED);
                }
                DAOFacade.bulkSave(tickets);
                DAOFacade.save(order);
                MailFacade.sendSuccessfulReservation(user, tickets);
            } else if ("failure".equals(status)) {
                order.setStatus(Order.Status.FAILURE);
                for(Ticket ticket: tickets) {
                    ticket.setStatus(Ticket.Status.INVALID);
                }
                DAOFacade.bulkSave(tickets);
                DAOFacade.save(order);
                MailFacade.sendFailedReservation(user);
            }
        }
    }
}
