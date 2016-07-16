package com.nagornyi.uc.action;

import com.nagornyi.uc.action.dev.ClearDBAction;
import com.nagornyi.uc.action.dev.RunScriptsAction;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nagorny
 * Date: 25.04.14
 */
public class ActionStorage {
    private static final Map<String, Class<? extends Action>> ACTIONS = new HashMap<String, Class<? extends Action>>();

    static {
        //POST /users
        ACTIONS.put("reg", RegisterAction.class);
        //POST /sessions
        ACTIONS.put("login", LoginAction.class);
        //DELETE /sessions
        ACTIONS.put("logout", LogoutAction.class);
        //POST /routes
        ACTIONS.put("addRoute", AddRouteAction.class);
        //GET /cities
        ACTIONS.put("allCities", GetAllCitiesAction.class);
        //POST /cities
        ACTIONS.put("addCity", AddCityAction.class);
        //GET /trips?
        ACTIONS.put("search", SearchTripsAction.class);
        ACTIONS.put("searchForWeekdays", SearchTripsForWeekdaysAction.class);
        //POST /users/{userId}/orders
        ACTIONS.put("order", OrderAction.class);
        //HEAD /users/{userId}/orders (200 - ok, 404 - not found)
        ACTIONS.put("orderExists", OrderExistsAction.class);
        //GET /currencies/EUR/UAH
        ACTIONS.put("welcome", WelcomeAction.class);
        //GET /users/{userId}/tickets?cursor={cursor}&count={count}
        ACTIONS.put("allTickets", GetAllTicketsAction.class);
        //POST /routes/{routeId}/trips/{tripId}/tickets TODO rewrite action to be smth like CreateTicketAction
        ACTIONS.put("lockSeat", LockTicketAction.class);
        //GET /routes/{routeId}/trips/{tripId}/tickets
        ACTIONS.put("ticketsForTrip", TicketsForTripAction.class);
        //DELETE /routes/{routeId}/trips/{tripId}/tickets/{ticketId}
        ACTIONS.put("removeTicket", RemoveTicketAction.class);
        //PUT /users/{username}/password
        ACTIONS.put("changePass", ChangePassAction.class);
        //GET /routes/{routeId}/trips/{type}/all/&ticketId={ticket} // TODO ticketId should be removed
        ACTIONS.put("otherTrips", GetOtherTripsAction.class);
        //POST /password
        ACTIONS.put("renewPass", RenewPasswordAction.class);
        //GET /routes/{routeId}/trips/{tripId}
        ACTIONS.put("getTrip", GetTripAction.class);
        //PATCH /routes/{routeId}/trips/all/tickets
        ACTIONS.put("saveTickets", SaveTicketsAction.class);

        ACTIONS.put("postFeedback", PostFeedback.class);

        /*Partner rights*/
        ACTIONS.put("changeDate", ChangeDateAction.class);

        /*Admin rights*/
        //POST /users
        ACTIONS.put("addUser", AddUserAction.class);
        //GET /users/:userId
        ACTIONS.put("getUser", GetUserAction.class);
        //PUT /users/:userId
        ACTIONS.put("updateUser", UpdateUserAction.class);
        ACTIONS.put("validateGoogleUser", ValidateGoogleUserAction.class);
        //GET /users/{username}/contacts
        ACTIONS.put("allContacts", GetContactsAction.class);
        // GET /routes/{routeId}/trips/{type}/all/tickets/size/{fromDate}/{toDate}
        ACTIONS.put("countPassengers", CountPassengersAction.class);
        //GET /routes/{routeId}/trips?date={date}
        ACTIONS.put("closestTripData", ClosestTripDataAction.class);
        //GET /routes/{routeId}/trips/{fromDate}/{toDate}
        ACTIONS.put("tripsForPeriod", GetTripsForPeriodAction.class);
        //GET /feedbacks
        ACTIONS.put("getFeedbacks", GetFeedbacksAction.class);

        ACTIONS.put("unlockTickets", UnlockTicketsAction.class);


        /*Test resources, admin rights*/
        //POST /database
        ACTIONS.put("run", RunScriptsAction.class);
        //DELETE /database
        ACTIONS.put("clearDB", ClearDBAction.class);
    }

    public static void register (String key, Class<? extends Action> actionClass) {
        ACTIONS.put(key, actionClass);
    }

    public static <T extends Action> T get(String key) {
        Class<? extends Action> actionClass = ACTIONS.get(key);
        if (actionClass == null) return null;

        try {
            return (T)actionClass.newInstance();
        } catch (Exception e) {
           throw new RuntimeException("Couldn't instantiate action " + key, e);
        }
    }
}
