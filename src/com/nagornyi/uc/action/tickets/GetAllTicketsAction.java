package com.nagornyi.uc.action.tickets;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.action.Action;
import com.nagornyi.uc.action.Authorized;
import com.nagornyi.uc.context.RequestContext;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.dao.PaginationBatch;
import com.nagornyi.uc.entity.Ticket;
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
        Locale locale = RequestContext.getLocale();
        String userEmail = req.getUserEmail();

        String cursor = req.getParam("cursor");
        Integer count = Integer.parseInt((String)req.getParam("count"));
        boolean first = false;
        //TODO could check cursor == null
        if (req.getParam("isInitialLoad") != null && Boolean.parseBoolean((String)(req.getParam("isInitialLoad")))) {
            first = true;
        }

        ITicketDAO ticketDAO = DAOFacade.getDAO(Ticket.class);
        PaginationBatch<Ticket> result = ticketDAO.getNextBatch(userEmail, cursor, count);
        List<Ticket> tickets = result.getEntitiesBatch();
        JSONObject respObj = new JSONObject();
        JSONArray ticketObjs = new JSONArray();
        for (Ticket ticket: tickets) {
            ticketObjs.put(ticket.toJson(locale));
        }

        respObj.put("objects", ticketObjs);
        boolean accepted = req.getUserRoleLevel() <= Role.PARTNER.level;
        respObj.put("isPartner", accepted);
        respObj.put("cursor", result.getStartCursor());

        if (first) {
            respObj.put("allPossibleCount", ticketDAO.countAllTicketsForUser(userEmail));
        }

        resp.setDataObject(respObj);
    }
}
