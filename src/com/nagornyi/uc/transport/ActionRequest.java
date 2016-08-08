package com.nagornyi.uc.transport;

import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IUserDAO;
import com.nagornyi.uc.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 * @author Nagorny
 * Date: 29.04.14
 */
public class ActionRequest {
    private HttpServletRequest realRequest;

    public ActionRequest(HttpServletRequest realRequest) {
        this.realRequest = realRequest;
    }

    public <T> T getParam(String name) {
        return (T)realRequest.getParameter(name);
    }

    public String serializeAllParams() {
        StringBuilder builder = new StringBuilder();
        Enumeration<String> names = realRequest.getParameterNames();
        while (names.hasMoreElements()) {
            String next = names.nextElement();
            builder.append(next).append("=").append(realRequest.getParameter(next)).append(", ");
        }
        return builder.toString();
    }

    public HttpSession getSession() {
        return realRequest.getSession();
    }


	public String getRemoteAddr() {
		return realRequest.getRemoteAddr();
	}

	public String getHeader(String name) {
		return realRequest.getHeader(name);
	}

    public boolean isAuthorized() {
        return realRequest.getSession(false) != null && getSession().getAttribute("email") != null;
    }

    public User getUser() {
        if (!isAuthorized()) return null;
        return ((IUserDAO)DAOFacade.getDAO(User.class)).getUserByEmail((String)getSession().getAttribute("email"));
    }

    public String getUserEmail() {
        return (String)getSession().getAttribute("email");
    }

    public Integer getUserRoleLevel() {
        return (Integer) getSession().getAttribute("role");
    }

    public boolean checkRequiredParams(String... keys) {
        for(String key: keys) if (getParam(key) == null) return false;
        return true;
    }
}
