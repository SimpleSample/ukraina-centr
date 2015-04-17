package com.nagornyi.uc.transport;

import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IUserDAO;
import com.nagornyi.uc.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

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

    public HttpSession getSession() {
        return realRequest.getSession();
    }

    public Locale getLocale() {
        String locale = (String)realRequest.getSession().getAttribute("loc");
        if (locale == null) return null;
        return Locale.forLanguageTag(locale);
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
}
