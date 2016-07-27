package com.nagornyi.uc.action;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.common.date.DateUtils;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITripDAO;
import com.nagornyi.uc.dto.trip.PricedTrip;
import com.nagornyi.uc.entity.Route;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.helper.TripConverter;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import static com.nagornyi.uc.action.ActionKeys.END_DATE_KEY;
import static com.nagornyi.uc.action.ActionKeys.START_DATE_KEY;

@Authorized(role = Role.ADMIN)
public class GetTripsForPeriodAction implements Action {
    private static final Logger LOG = Logger.getLogger(GetTripsForPeriodAction.class.getName());

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {

        Calendar startDate = setupStartDate(req);
        Calendar endDate = setupEndDate(req);

        LOG.info("Start date: " + startDate.getTimeInMillis() + ", end Date: " + endDate.getTimeInMillis());

        Route route = DAOFacade.findAll(Route.class).get(0);
        ITripDAO tripDAO = DAOFacade.getDAO(Trip.class);
        List<PricedTrip> tripArray = new ArrayList<>();
        List<Trip> trips = tripDAO.getTripsByDateRange(route, startDate.getTime(), endDate.getTime());

        for (Trip trip: trips) {
            tripArray.add(TripConverter.convertToPricedTrip(trip));
        }
        resp.setData(tripArray);
    }

    private static Calendar setupStartDate(ActionRequest req) {
        String startDateMiliseconds = req.getParam(START_DATE_KEY);
        Calendar startDate = Calendar.getInstance();
        LOG.info("Current time: " + startDate.getTimeInMillis());

        if (startDateMiliseconds != null) {
            startDate.setTimeInMillis(Long.valueOf(startDateMiliseconds));
        }
        startDate.setTime(DateUtils.getUkraineDateFor(startDate.getTime()));

        return startDate;
    }

    private static Calendar setupEndDate(ActionRequest req) {
        String endDateMiliseconds = req.getParam(END_DATE_KEY);
        Calendar endDate = Calendar.getInstance();

        if (endDateMiliseconds != null) {
            endDate.setTimeInMillis(Long.valueOf(endDateMiliseconds));
        } else {
            endDate.add(Calendar.DAY_OF_MONTH, 90/*days*/); // todo configure
        }
        endDate.setTime(DateUtils.getUkraineDateFor(endDate.getTime()));

        return endDate;
    }

}
