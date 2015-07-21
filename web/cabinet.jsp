<%@ page import="java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    ResourceBundle bundle = ResourceBundle.getBundle("i18n.cabinet", Locale.forLanguageTag("uk")); //TODO localization issue
%>
<!DOCTYPE html>
<html>
<head>
    <title><%=bundle.getString("cabinet.cabinet")%></title>
    <jsp:include page="/WEB-INF/jsp/imports.jsp" flush="true"/>
    <jsp:include page="/WEB-INF/jsp/i18n.jsp" flush="true">
        <jsp:param name="bundleName" value="client_cabinet"/>
    </jsp:include>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ng/cabinet.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ng/tablesaw.custom.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tablesaw.css"/>
    <script src="${pageContext.request.contextPath}/js/tablesaw.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/ng/ng.pager.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/ng/ng.cabinet.js" type="text/javascript"></script>
</head>
<body class="content-fixed">
<jsp:include page="/WEB-INF/jsp/header.jsp" flush="true">
    <jsp:param name="activeTab" value=""/>
</jsp:include>

<div class="container-fluid page-content" style="min-height: 400px;">
    <div class="row page-content-wrapper">
        <div class="col-sm-3 col-md-2 profile-menu">
            <ul class="profile-menu-list">
                <li id="order-history" class="profile-menu-list__item green">
                    <span class="profile-menu-list__item_text">Історія замовлень</span>
                </li>
                <li id="password-change" class="profile-menu-list__item yellow">
                    <span class="profile-menu-list__item_text">Зміна паролю</span>
                </li>
                <li id="personal-data" class="profile-menu-list__item red">
                    <span class="profile-menu-list__item_text">Особисті дані</span>
                </li>
                <li id="cabinet-logout" class="profile-menu-list__item blue">
                    <span class="profile-menu-list__item_text">Вихід</span>
                </li>
            </ul>
        </div>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 user-board">
            <div id="message-container"></div>
            <h2><%=bundle.getString("cabinet.personal_cabinet")%></h2>
            <div class="profile-workplace">
                <div class="history-table">
                    <table class="tablesaw tablesaw-sortable" data-tablesaw-mode="stack" data-tablesaw-sortable="">
                        <thead>
                        <tr>
                            <th data-tablesaw-sortable-col data-tablesaw-sortable-switch><%=bundle.getString("cabinet.passenger")%></th>
                            <th><%=bundle.getString("cabinet.phones")%></th>
                            <th data-tablesaw-sortable-col data-tablesaw-sortable-switch><%=bundle.getString("cabinet.route")%></th>
                            <th data-tablesaw-sortable-col data-tablesaw-sortable-switch><%=bundle.getString("cabinet.departure_date")%></th>
                            <th><%=bundle.getString("cabinet.seat")%></th>
                            <th data-tablesaw-sortable-col data-tablesaw-sortable-switch><%=bundle.getString("cabinet.status")%></th>
                            <th>&nbsp;</th>
                        </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                    <div class="tablesaw-pager"></div>
                </div>
            </div>
            <div class="feedback-circle"><span class="glyphicon glyphicon-bullhorn" title="Залишити відгук" aria-hidden="true"></span></div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/footer.jsp" flush="true"/>
</body>
</html>
