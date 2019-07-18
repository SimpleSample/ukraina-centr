package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.nagornyi.uc.common.PurchaseResult;
import com.nagornyi.uc.common.UserFriendlyException;
import com.nagornyi.uc.common.liqpay.LiqPay;
import com.nagornyi.uc.common.liqpay.LiqPayRequest;
import com.nagornyi.uc.common.mail.MailFacade;
import com.nagornyi.uc.context.RequestContext;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.entity.*;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;
import com.nagornyi.uc.util.ActionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import static com.nagornyi.uc.Constants.*;

/**
 * @author Nagorny
 * Date: 25.05.14
 */
@Authorized
public class OrderAction implements Action {
    private static Logger log = Logger.getLogger(OrderAction.class.getName());

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        ActionUtil.checkRequired(req, ORDER);

        JSONObject orderObj = new JSONObject((String)req.getParam(ORDER));

        if (!ORDER_TYPE_RESERVE.equals(orderObj.getString(ORDER_TYPE))) {
            return;
        }

        User user = req.getUser();
        Order order = new Order(user);
        DAOFacade.save(order);
        log.info("Processing order for user " + user.getUsername());
        PurchaseResult result = reserve(orderObj, user, order);
        if (result.hasAnyFailed()) {
            throw new UserFriendlyException(processFailed(result, RequestContext.getLocale()));
        } else {
            DAOFacade.bulkSave(result.getAllTickets());
            if (user.isPartner() || user.isAdmin()) {
                order.succeeded();
                MailFacade.sendSuccessfulReservation(user, result.getAllTickets());
            } else {
                double resultPrice = 0;
                for (Ticket ticket: result.getAllTickets()) {
                    resultPrice += ticket.getCalculatedPrice();
                }
                log.info("Calculated price for order " + resultPrice);

                String paymentDescription = LiqPay.getPaymentDescription(order, user, result.getAllTickets());
                LiqPayRequest liqPayRequest = LiqPay.createLiqPayRequest(resultPrice, order.getStringKey(), paymentDescription);
                log.info("Liq pay request: " + liqPayRequest.toString());
                resp.setData(liqPayRequest);
            }
            DAOFacade.save(order);
        }
    }

    public PurchaseResult reserve(JSONObject orderObj, User currentUser, Order order) throws JSONException {
        String userForthTripKey = orderObj.getString("tripId");
        Trip userForthTrip = DAOFacade.findByKey(Trip.class, KeyFactory.stringToKey(userForthTripKey));

        String userBackTripKey = orderObj.has("backTripId")? orderObj.getString("backTripId") : null;
        Trip userBackTrip = userBackTripKey == null? null : DAOFacade.findByKey(Trip.class, KeyFactory.stringToKey(userBackTripKey));

        PurchaseResult result = new PurchaseResult(userForthTrip, userBackTrip);
        ITicketDAO dao = DAOFacade.getDAO(Ticket.class);

        JSONObject tickets = orderObj.getJSONObject("tickets");
        JSONArray ticketIds = tickets.names();
        for (int i = 0, size = ticketIds.length(); i < size; i++) {
            JSONObject ticketObj = tickets.getJSONObject(ticketIds.getString(i));

            String passenger = ticketObj.getString("passenger");
            String phone1 = hasNonNullValue(ticketObj, "phone1")? ticketObj.getString("phone1") : null;
            String phone2 = hasNonNullValue(ticketObj, "phone2")? ticketObj.getString("phone2") : null;
            String startCityId = ticketObj.getString("startCity");
            String endCityId = ticketObj.getString("endCity");
            Date startDate = new Date(ticketObj.getLong("rawStartDate"));
            DiscountCategory category = DiscountCategory.valueOf(ticketObj.getString("discountId"));
            String userForthTicketId = ticketObj.has("ticketId")? (String)ticketObj.get("ticketId") : null;
            String userForthSeatId = (String)ticketObj.get("seatId");
            Seat userForthSeat = DAOFacade.findById(Seat.class, KeyFactory.stringToKey(userForthSeatId));
            String note = hasNonNullValue(ticketObj, "note")? (String) ticketObj.get("note") : null;

            Ticket ticket = dao.createReservedTicket(userForthTicketId,
                                                    userForthTrip,
                                                    userForthSeat,
                                                    passenger,
                                                    phone1,
                                                    phone2,
                                                    currentUser,
                                                    startCityId,
                                                    endCityId,
                                                    startDate,
                                                    userBackTrip != null,
                                                    category,
                                                    order,
                                                    note);

            if (ticket != null) {
                result.addTicket(ticket);
            } else {
                result.addForthFailed(ticketObj.getString("seatNum"));
            }
            if (userBackTrip != null) {
                Date userBackStartDate = new Date(ticketObj.getLong("rawBackStartDate"));
                String userBackTicketId = ticketObj.has("backTicketId")? (String)ticketObj.get("backTicketId") : null;
                String userBackSeatId = (String)ticketObj.get("backSeatId");
                Seat userBackSeat = DAOFacade.findById(Seat.class, KeyFactory.stringToKey(userBackSeatId));

                Ticket backTicket = dao.createReservedTicket(userBackTicketId,
                                                            userBackTrip,
                                                            userBackSeat,
                                                            passenger,
                                                            phone1,
                                                            phone2,
                                                            currentUser,
                                                            endCityId,
                                                            startCityId,
                                                            userBackStartDate,
                                                            true,
                                                            category,
                                                            order,
                                                            note);

                if(backTicket != null) {
                    result.addTicket(backTicket);
                } else {
                    result.addBackFailed(ticketObj.getString("backSeatNum"));
                }
            }
        }
        return result;
    }

    private String processFailed(PurchaseResult result, Locale locale) throws JSONException {
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

        resultString += " вже придбані";
        return resultString;
    }

    private String failedTicketsToString(List<String> failedTickets, Trip trip, Locale loc) {
        return StringUtils.join(failedTickets, ", ") +
                " ("+trip.getRoute().getFirstCity().getLocalizedName(loc) + " - " +
                trip.getRoute().getLastCity().getLocalizedName(loc) +")";
    }

    private boolean hasNonNullValue(JSONObject object, String key) {
        try {
            return object.has(key) && object.get(key) != JSONObject.NULL;
        } catch (JSONException e) {
            log.severe("Parsing failed");
        }
        return false;
    }
}
