package com.nagornyi.uc.action.seats;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.nagornyi.uc.action.Action;
import com.nagornyi.uc.action.Authorized;
import com.nagornyi.uc.action.response.GetAllSeatsResponse;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ISeatDAO;
import com.nagornyi.uc.dto.SeatDto;
import com.nagornyi.uc.entity.Seat;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.ArrayList;
import java.util.List;

@Authorized
public class GetSeatsAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        ISeatDAO seatDAO = DAOFacade.getDAO(Seat.class);
        List<Seat> seats = seatDAO.getSeatsForSetra();
        List<SeatDto> allSeats = new ArrayList<>(seats.size());
        for (Seat seat: seats) {
            allSeats.add(new SeatDto(seat.getStringKey(), seat.getSeatNum()));
        }
        resp.setData(new GetAllSeatsResponse(allSeats));
    }
}
