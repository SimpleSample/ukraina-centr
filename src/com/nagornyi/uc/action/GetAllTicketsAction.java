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
        boolean first = false;
        if (req.getParam("isInitialLoad") != null && Boolean.parseBoolean((String)(req.getParam("isInitialLoad")))) {
            first = true;
        }

        ITicketDAO ticketDAO = DAOFacade.getDAO(Ticket.class);
        PaginationBatch<Ticket> result = ticketDAO.getNextBatch(user, cursor, count);
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

        if (first) {
            respObj.put("allPossibleCount", ticketDAO.countAllTicketsForUser(user));
        }

        resp.setDataObject(respObj);
    }
}
