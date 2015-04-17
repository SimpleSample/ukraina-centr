package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.common.ReservationResult;
import com.nagornyi.uc.common.UserFriendlyException;
import com.nagornyi.uc.common.liqpay.LiqPay;
import com.nagornyi.uc.common.mail.MailFacade;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.entity.*;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;
import com.nagornyi.uc.util.ActionUtil;
import com.nagornyi.uc.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.nagornyi.uc.Constants.*;

/**
 * @author Nagorny
 * Date: 25.05.14
 */
@Authorized
public class OrderAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        ActionUtil.checkRequired(req, ORDER);

        JSONObject orderObj = new JSONObject((String)req.getParam(ORDER));

        if (ORDER_TYPE_RESERVE.equals(orderObj.getString(ORDER_TYPE))) {
            User user = req.getUser();
            Order order = new Order(user);
            DAOFacade.save(order);
            ReservationResult result = reserve(orderObj, user, order);
            if (result.hasAnyFailed()) {
                throw new UserFriendlyException(processFailed(result, req.getLocale()));
            } else {
                DAOFacade.bulkSave(result.getAllTickets());
                if (user.isPartner()) {
                    order.setStatus(Order.Status.SUCCESS);
                    MailFacade.sendSuccessfulReservation(user, result.getAllTickets());
                } else {
                    JSONObject liqPayParams = LiqPay.getLiqPayReservationJSON(result.getAllTickets().size(), order.getStringKey());
                    resp.setDataObject(liqPayParams);
                }
                DAOFacade.save(order);
            }
        }
    }

    public ReservationResult reserve(JSONObject orderObj, User currentUser, Order order) throws JSONException {
        String forthTripKey = orderObj.getString("forthTripId");
        Trip forthTrip = DAOFacade.findByKey(Trip.class, KeyFactory.stringToKey(forthTripKey));

        String backTripKey = orderObj.has("backTripId")? orderObj.getString("backTripId") : null;
        Trip backTrip = backTripKey == null? null : DAOFacade.findByKey(Trip.class, KeyFactory.stringToKey(backTripKey));

        ReservationResult result = new ReservationResult(forthTrip, backTrip);
        ITicketDAO dao = DAOFacade.getDAO(Ticket.class);

        JSONArray tickets = orderObj.getJSONArray("tickets");
        for (int i = 0, size = tickets.length(); i < size; i++) {
            JSONObject ticketObj = tickets.getJSONObject(i);

            String passenger = ticketObj.getString("passenger");
            String phone1 = ticketObj.has("phone1")? ticketObj.getString("phone1") : null;
            String phone2 = ticketObj.has("phone2")? ticketObj.getString("phone2") : null;
            String startCityId = ticketObj.getString("startCity");
            String endCityId = ticketObj.getString("endCity");
            Date startDate = new Date(ticketObj.getLong("rawStartDate"));
            DiscountCategory category = DiscountCategory.valueOf(ticketObj.getString("discountId"));
            String forthTicketId = ticketObj.has("forthTicketId")? (String)ticketObj.get("forthTicketId") : null;
            String forthSeatId = (String)ticketObj.get("forthSeatId");
            Seat forthSeat = DAOFacade.findById(Seat.class, KeyFactory.stringToKey(forthSeatId));

            Ticket ticket = dao.createReservedTicket(forthTicketId, forthTrip, forthSeat, passenger, phone1, phone2, currentUser,
                    startCityId, endCityId, startDate, backTrip != null, category, order);

            if (ticket != null) {
                result.addTicket(ticket);
            } else {
                result.addForthFailed(ticketObj.getString("forthSeatNum"));
            }
            if (backTrip != null) {
                Date backStartDate = new Date(ticketObj.getLong("rawBackStartDate"));
                String backTicketId = ticketObj.has("backTicketId")? (String)ticketObj.get("backTicketId") : null;
                String backSeatId = (String)ticketObj.get("backSeatId");
                Seat backSeat = DAOFacade.findById(Seat.class, KeyFactory.stringToKey(backSeatId));

                Ticket backTicket = dao.createReservedTicket(backTicketId, backTrip, backSeat, passenger, phone1, phone2, currentUser,
                        endCityId, startCityId, backStartDate, true, category, order);

                if(backTicket != null) {
                    result.addTicket(backTicket);
                } else {
                    result.addBackFailed(ticketObj.getString("backSeatNum"));
                }
            }
        }
        return result;
    }

    private String processFailed(ReservationResult result, Locale locale) throws JSONException {
        Trip forthTrip = result.getForthTrip();
        Trip backTrip = result.getBackTrip();
        String resultString = "На жаль, місця ";

        if (!result.getForthFailedTickets().isEmpty()) {
            resultString += failedTicketsToString(result.getForthFailedTickets(), forthTrip, locale);
        }
        if (!result.getForthFailedTickets().isEmpty() && !result.getBackFailedTickets().isEmpty()) {
            resultString += ", а також ";
        }
        if (backTrip != null && !result.getBackFailedTickets().isEmpty()) {
            resultString += failedTicketsToString(result.getBackFailedTickets(), backTrip, locale);
        }

        resultString += " вже заброньовані";
        return resultString;
    }

    private String failedTicketsToString(List<String> failedTickets, Trip trip, Locale loc) {
        return StringUtils.join(failedTickets, ", ") +
                " ("+trip.getRoute().getFirstCity().getLocalizedName(loc) + " - " +
                trip.getRoute().getLastCity().getLocalizedName(loc) +")";
    }
}
