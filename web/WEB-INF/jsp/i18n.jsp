<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script>
    var clientBundle = clientBundle || {};
    <%  String bundleName = request.getParameter("bundleName");
        ResourceBundle bundle = ResourceBundle.getBundle("i18n."+bundleName, Locale.forLanguageTag("uk")); //TODO localization issue
        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = bundle.getString(key);
        %>clientBundle['<%=key%>'] = '<%=value%>';
    <%}%>
</script>