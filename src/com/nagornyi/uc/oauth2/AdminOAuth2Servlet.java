package com.nagornyi.uc.oauth2;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.http.GenericUrl;
import com.nagornyi.uc.service.ServiceLocator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Created by artemnagorny on 17.08.15.
 *
 * Info
 * https://developers.google.com/google-apps/contacts/v3/#authorizing_requests_with_oauth_20
 *
 */
public class AdminOAuth2Servlet extends AbstractAuthorizationCodeServlet {
    private static final Logger log = Logger.getLogger(AdminOAuth2Servlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        log.info("sending to tickets page");
        response.sendRedirect("/admin/tickets?oauth=1");
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
        return ServiceLocator.getInstance().getUserService().getAdminEmail();
    }
}
