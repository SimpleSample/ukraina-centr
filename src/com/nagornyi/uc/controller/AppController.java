package com.nagornyi.uc.controller;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
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
        boolean gotError = false;
        RequestContext.init(req);

        Authorized auth = action.getClass().getAnnotation(Authorized.class);
        if (auth != null) {
            HttpSession session = req.getSession(false);
            if (session == null || req.getSession().getAttribute("email") == null) {
                setErrorMessage(responseObj, "Авторизуйтесь, будь ласка, в системі");
                log("No user in session");
                gotError = true;
            } else {
                String email = (String)req.getSession().getAttribute("email");
				Integer role = (Integer)req.getSession().getAttribute("role");
                if (role == null || auth.role().level < role) {
                    setErrorMessage(responseObj, "Авторизуйтесь, будь ласка, в системі");
                    log("user " + email + " has inappropriate role: " + role);
                    gotError = true;
                }
            }
        }

        if (!gotError) {

            try {
                log("Performing Action " + action.getClass().getSimpleName());
                action.perform(request, response);
            } catch (UserFriendlyException e) {
                log("Couldn't perform action " + actionAlias, e);
                setErrorMessage(responseObj, e.getUserFriendlyMessage());
            } catch (Exception e) {
                log("Couldn't perform action " + actionAlias, e);
                setErrorMessage(responseObj, "Упс, щось пішло не так"); //TODO localize
            }
        }

        if (response.getDataObj() != null) {
            try {
                responseObj.put("data", response.getDataObj());
            } catch (JSONException e) {}
        }
        resp.getWriter().print(responseObj.toString());
        RequestContext.destroy();
    }

    private void setErrorMessage(JSONObject responseObj, String message) {
        try {
            responseObj.put("errorMessage", message);
        } catch (JSONException e) {}
    }
}
