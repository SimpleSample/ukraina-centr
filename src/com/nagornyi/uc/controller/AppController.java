package com.nagornyi.uc.controller;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.apphosting.api.ApiProxy;
import com.nagornyi.uc.action.Action;
import com.nagornyi.uc.action.ActionStorage;
import com.nagornyi.uc.action.Authorized;
import com.nagornyi.uc.common.UserFriendlyException;
import com.nagornyi.uc.context.RequestContext;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Nagorny
 * Date: 25.04.14
 */
public class AppController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String actionAlias = req.getParameter("a");
        if (actionAlias == null) return;

        Action action = ActionStorage.get(actionAlias);
        if (action == null) return;

        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Type", "application/json");

        ActionRequest request = new ActionRequest(req);
        JSONObject responseObj = new JSONObject();
        ActionResponse response = new ActionResponse(responseObj);
        RequestContext.init(req);

        boolean isAuthorized = checkAuthorization(req, action, responseObj);

        if (isAuthorized) {
            performAction(action, request, response, responseObj, actionAlias);
        }

        if (response.getDataObj() != null) {
            try {
                responseObj.put("data", response.getDataObj());
            } catch (JSONException e) {}
        }
        resp.getWriter().print(responseObj.toString());
        RequestContext.destroy();
    }

    private boolean checkAuthorization(HttpServletRequest req, Action action, JSONObject responseObject) {
        Authorized auth = action.getClass().getAnnotation(Authorized.class);

        if (auth == null) {
            return true;
        }
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            setErrorMessage(responseObject, "Авторизуйтесь, будь ласка, в системі");
            log("No user in session");
            return false;
        } else {
            String email = (String) session.getAttribute("email");
            Integer role = (Integer) session.getAttribute("role");
            if (role == null || auth.role().level < role) {
                setErrorMessage(responseObject, "Авторизуйтесь, будь ласка, в системі");
                log("user " + email + " has inappropriate role: " + role);
                return false;
            }
        }
        return true;
    }

    private void performAction(Action action, ActionRequest request, ActionResponse response,
                               JSONObject responseObject, String actionAlias) {
        try {
            log("Performing Action " + action.getClass().getSimpleName() +". \n Params: " + request.serializeAllParams());
            action.perform(request, response);
        } catch (UserFriendlyException e) {
            log("Couldn't perform action "+actionAlias+" - business exception", e);
            setErrorMessage(responseObject, e.getUserFriendlyMessage());
        } catch (ApiProxy.OverQuotaException e) {
            //TODO could try to send email about qouta
            log("Couldn't perform action "+actionAlias+" - quota limit exceeded ", e);
            setErrorMessage(responseObject, "Сервіс тимчасово недоступний");
        } catch (Exception e) {
            log("Couldn't perform action " + actionAlias, e);
            setErrorMessage(responseObject, "Упс, щось пішло не так"); //TODO localize
        }
    }

    private void setErrorMessage(JSONObject responseObj, String message) {
        try {
            responseObj.put("errorMessage", message);
        } catch (JSONException e) {}
    }
}
