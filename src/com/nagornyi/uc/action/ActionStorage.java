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
        ACTIONS.put("reg", RegisterAction.class);
        ACTIONS.put("login", LoginAction.class);
        ACTIONS.put("logout", LogoutAction.class);
        ACTIONS.put("sendMail", SendMail.class);
        ACTIONS.put("addRoute", AddRouteAction.class);
        ACTIONS.put("allCities", GetAllCitiesAction.class);
        ACTIONS.put("addCity", AddCityAction.class);
        ACTIONS.put("run", RunScriptsAction.class);
        ACTIONS.put("clearDB", ClearDBAction.class);
        ACTIONS.put("search", SearchTripsAction.class);
        ACTIONS.put("searchForWeekdays", SearchTripsForWeekdaysAction.class);
        ACTIONS.put("order", OrderAction.class);
        ACTIONS.put("orderExists", OrderExistsAction.class);
        ACTIONS.put("welcome", WelcomeAction.class);
        ACTIONS.put("allTickets", GetAllTicketsAction.class);
        ACTIONS.put("lockSeat", LockSeatAction.class);
        ACTIONS.put("ticketsForTrip", TicketsForTripAction.class);
        ACTIONS.put("addUser", AddUserAction.class);
        ACTIONS.put("removeTicket", RemoveTicketAction.class);
        ACTIONS.put("changePass", ChangePassAction.class);
        ACTIONS.put("otherTrips", GetTripsAction.class);
        ACTIONS.put("changeDate", ChangeDateAction.class);
        ACTIONS.put("renewPass", RenewPasswordAction.class);
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
