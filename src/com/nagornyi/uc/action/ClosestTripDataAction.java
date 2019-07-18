package com.nagornyi.uc.action;

import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITripDAO;
import com.nagornyi.uc.entity.Route;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.helper.TripConverter;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.Date;

import static com.nagornyi.uc.action.ActionKeys.BACK_TRIP_KEY;
import static com.nagornyi.uc.action.ActionKeys.FORTH_TRIP_KEY;
/**
 * Created by artemnagorny on 04.08.15.
 */
@Authorized(role = Role.ADMIN)
public class ClosestTripDataAction implements Action {


    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        Route route = DAOFacade.findAll(Route.class).get(0);

        ITripDAO dao = DAOFacade.getDAO(Trip.class);
        Date date = new Date();
        Trip forth = dao.getOrCreateTrip(route, date, true);
        Trip back = dao.getOrCreateTrip(route, date, false);

        JSONObject respObject = new JSONObject();
        respObject.put(FORTH_TRIP_KEY, TripConverter.convertTripWithTicketsExcludeAdmin(forth));
        respObject.put(BACK_TRIP_KEY, TripConverter.convertTripWithTicketsExcludeAdmin(back));
        resp.setDataObject(respObject);
    }

}
