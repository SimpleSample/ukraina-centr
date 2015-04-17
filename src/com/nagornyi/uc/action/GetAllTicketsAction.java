package com.nagornyi.uc.action;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.dao.PaginationBatch;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.List;
import java.util.Locale;

/**
 * @author Nagornyi
 * Date: 02.06.14
 */
@Authorized
public class GetAllTicketsAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        Locale locale = req.getLocale();
        User user = req.getUser();

        String cursor = req.getParam("cursor");
        Integer count = Integer.parseInt((String)req.getParam("count"));

        PaginationBatch<Ticket> result = ((ITicketDAO)DAOFacade.getDAO(Ticket.class)).getNextBatch(user, cursor, count);
//        Collections.sort(tickets, new Comparator<Ticket>() {
//            @Override
//            public int compare(Ticket o1, Ticket o2) {
//                if (o1.getStartDate().getTime() > o2.getStartDate().getTime()) return 1;
//                else return -1;
//            }
//        });
        List<Ticket> tickets = result.getEntitiesBatch();
        JSONObject respObj = new JSONObject();
        JSONArray ticketObjs = new JSONArray();
        for (Ticket ticket: tickets) {
            ticketObjs.put(ticket.toJSON(locale));
        }

        respObj.put("objects", ticketObjs);
        boolean accepted = user.getRole().level <= Role.PARTNER.level;
        respObj.put("isPartner", accepted);
        respObj.put("cursor", result.getStartCursor());

        resp.setDataObject(respObj);
    }
}
