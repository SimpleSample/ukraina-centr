package com.nagornyi.uc.oauth2;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.http.GenericUrl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Created by artemnagorny on 17.08.15.
 */
public class AdminOAuth2CallbackServlet extends AbstractAuthorizationCodeCallbackServlet {
    private static final Logger log = Logger.getLogger(AdminOAuth2CallbackServlet.class.getName());

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
            throws ServletException, IOException {
        log.info("in callback onsuccess redirection");
        resp.sendRedirect("/retrieveContacts");
    }

    @Override
    protected void onError(
            HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
            throws ServletException, IOException {
        log.info("in callback onerror");
    }

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath("/oauth2callback");
        return url.build();
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return AuthUtil.initFlow(Collections.singleton("https://www.google.com/m8/feeds/"));
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
        return "info@ukraina-centr.com";
    }


}
