package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.helper.TripConverter;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

/**
 * Created by artemnagorny on 15.08.15.
 */
@Authorized(role = Role.ADMIN)
public class GetTripAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        if (!req.checkRequiredParams(ActionKeys.TRIP_ID_KEY)) {
            return;
        }
        String tripId = req.getParam(ActionKeys.TRIP_ID_KEY);

        Trip trip = DAOFacade.findById(Trip.class, KeyFactory.stringToKey(tripId));
        JSONObject tripObj = TripConverter.convertTripWithTickets(trip);
        resp.setResponseParam(ActionKeys.TRIP_KEY, tripObj);
    }
}
