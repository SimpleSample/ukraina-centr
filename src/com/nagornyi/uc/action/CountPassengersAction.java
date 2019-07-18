package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.common.date.DateFormatter;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITripDAO;
import com.nagornyi.uc.dao.app.TicketDAO;
import com.nagornyi.uc.entity.Route;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.*;
import java.util.logging.Logger;
import static com.nagornyi.uc.action.ActionKeys.*;
/**
 * Created by artemnagorny on 03.08.15.
 */
@Authorized(role = Role.ADMIN)
public class CountPassengersAction implements Action {
    private static final Logger log = Logger.getLogger(CountPassengersAction.class.getName());

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        if (!req.checkRequiredParams(START_DATE_KEY, END_DATE_KEY)) {
            return;
        }
        boolean isForth = FORTH_KEY.equals(req.getParam(TRIP_TYPE_KEY));
        String startDate = req.getParam(START_DATE_KEY);
        Long startTime = Long.valueOf(startDate);
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startTime);

        String endDate = req.getParam(END_DATE_KEY);
        Long endTime = Long.valueOf(endDate);
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(endTime);

        Route route = DAOFacade.findAll(Route.class).get(0);

        ITripDAO dao = DAOFacade.getDAO(Trip.class);
        List<Trip> trips = dao.getTripsByDateRange(route, start.getTime(), end.getTime(), isForth);

        JSONArray dataArray = new JSONArray();
        JSONArray milissDataArray = new JSONArray();

        for (Trip trip: trips) {
            TicketDAO ticketDAO = DAOFacade.getDAO(Ticket.class);
            Query query = ticketDAO.getQueryByParent(trip.getKey());
            int ticketsCount = ticketDAO.countForQuery(query);
            JSONArray tripArray = new JSONArray();
            Date tripStartDate = trip.getStartDate();
            tripArray.put(DateFormatter.defaultDayMonthFormat(tripStartDate));
            tripArray.put(ticketsCount);
            Long tripStartMiliss = tripStartDate.getTime();
            dataArray.put(tripArray);
            milissDataArray.put(tripStartMiliss);
        }
        JSONObject result = new JSONObject();
        result.put(DATA_ARRAY_KEY, dataArray);
        result.put(MILISS_DATA_ARRAY_KEY, milissDataArray);
        resp.setDataObject(result);
    }
}
