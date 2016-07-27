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
import com.nagornyi.uc.transport.ResponseDto;
import com.nagornyi.uc.util.ActionUtil;

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
        ResponseDto responseObj = new ResponseDto();
        ActionResponse response = new ActionResponse();
        RequestContext.init(req);

        boolean isAuthorized = checkAuthorization(req, action, responseObj);

        if (isAuthorized) {
            performAction(action, request, response, responseObj, actionAlias);
        }

        writeResponse(responseObj, response, resp);

        RequestContext.destroy();
    }

    private void writeResponse(ResponseDto responseDto, ActionResponse response, HttpServletResponse resp) throws IOException {
        //new
        if (responseDto.getErrorMessage() != null || response.getData() != null) {
            responseDto.setData(response.getData());
            resp.getWriter().print(ActionUtil.serializeObject(responseDto));
        //old, will be removed
        } else if (response.getDataObj() != null) {
            JSONObject responseJson = new JSONObject();
            try {
                responseJson.put("data", response.getDataObj());
            } catch (JSONException e) {}

            resp.getWriter().print(responseJson.toString());
        } else {
            resp.getWriter().print("{}");
        }
    }

    private boolean checkAuthorization(HttpServletRequest req, Action action, ResponseDto responseObject) {
        Authorized auth = action.getClass().getAnnotation(Authorized.class);

        if (auth == null) {
            return true;
        }
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            responseObject.setErrorMessage("Авторизуйтесь, будь ласка, в системі");
            log("No user in session");
            return false;
        } else {
            String email = (String) session.getAttribute("email");
            Integer role = (Integer) session.getAttribute("role");
            if (role == null || auth.role().level < role) {
                responseObject.setErrorMessage("Авторизуйтесь, будь ласка, в системі");
                log("user " + email + " has inappropriate role: " + role);
                return false;
            }
        }
        return true;
    }

    private void performAction(Action action, ActionRequest request, ActionResponse response,
                               ResponseDto responseObject, String actionAlias) {
        try {
            log("Performing Action " + action.getClass().getSimpleName() +". \n Params: " + request.serializeAllParams());
            action.perform(request, response);
        } catch (UserFriendlyException e) {
            log("Couldn't perform action "+actionAlias+" - business exception", e);
            responseObject.setErrorMessage(e.getUserFriendlyMessage());
        } catch (ApiProxy.OverQuotaException e) {
            //TODO could try to send email about qouta
            log("Couldn't perform action "+actionAlias+" - quota limit exceeded ", e);
            responseObject.setErrorMessage("Сервіс тимчасово недоступний");
        } catch (Exception e) {
            log("Couldn't perform action " + actionAlias, e);
            responseObject.setErrorMessage("Упс, щось пішло не так"); //TODO localize
        }
    }
}
