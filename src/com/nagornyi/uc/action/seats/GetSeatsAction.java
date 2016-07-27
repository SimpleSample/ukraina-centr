package com.nagornyi.uc.action.seats;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.action.Action;
import com.nagornyi.uc.action.Authorized;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ISeatDAO;
import com.nagornyi.uc.entity.Seat;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.List;

import static com.nagornyi.uc.action.ActionKeys.ALL_SEATS_KEY;

@Authorized
public class GetSeatsAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        ISeatDAO seatDAO = DAOFacade.getDAO(Seat.class);
        List<Seat> seats = seatDAO.getSeatsForSetra();
        JSONArray allSeats = new JSONArray();
        for (Seat seat: seats) {
            allSeats.put(seat.toJSON());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ALL_SEATS_KEY, allSeats);
        resp.setDataObject(jsonObject);
    }
}
