<%@ page import="java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.nagornyi.uc.i18n.I18n" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    ResourceBundle bundle = I18n.getBundle("i18n.about", Locale.forLanguageTag("uk")); //TODO localization issue
%>
<!DOCTYPE html>
<html>
<head>
    <title><%=bundle.getString("about.about_us")%></title>
    <jsp:include page="/WEB-INF/jsp/imports.jsp" flush="true"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp" flush="true">
    <jsp:param name="activeTab" value="about"/>
</jsp:include>

<div class="container-fluid page-content">
    <div class="container pad-container">
        <article>
            <h2><%=bundle.getString("about.lets_travel")%></h2>
            <p class="p2"><%=bundle.getString("about.first_paragraph1")%> <a class="link italic" href="${pageContext.request.contextPath}/leasing"><%=bundle.getString("about.first_paragraph2")%></a>. <%=bundle.getString("about.first_paragraph3")%></p>
            <p class="p3"><%=bundle.getString("about.second_paragraph")%></p>
            <p><a href="/conditions"><%=bundle.getString("about.read_more_about_passengers_transportation_policy")%> »</a></p>
            <div class="article-photo"><img src="images/photos/DSC_2686_ph.jpg"></div>
        </article>
    </div>
</div>


<jsp:include page="/WEB-INF/jsp/footer.jsp" flush="true"/>
</body>
</html>
