package com.nagornyi.test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * https://developers.google.com/appengine/docs/java/datastore/queries?hl=ru
 * @author Nagorny
 *         Date: 06.02.14
 */
public class TestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        resp.setHeader("Access-Control-Allow-Origin", "*");
        out.print("<div>server response</div>");
    }
}
