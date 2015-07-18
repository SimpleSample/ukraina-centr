<%@ page import="java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    ResourceBundle bundle = ResourceBundle.getBundle("i18n.cabinet", Locale.forLanguageTag("uk")); //TODO localization issue
%>
<html>
<head>
    <title><%=bundle.getString("cabinet.cabinet")%></title>
    <jsp:include page="/WEB-INF/jsp/imports.jsp" flush="true"/>
    <jsp:include page="/WEB-INF/jsp/i18n.jsp" flush="true">
        <jsp:param name="bundleName" value="client_cabinet"/>
    </jsp:include>
    <%--<link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/themes/smoothness/jquery-ui.css" />--%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/markup/cabinet.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tablesaw.css"/>
    <%--<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>--%>
    <script src="js/tablesaw.js" type="text/javascript"></script>
    <script src="js/ng/ng.pager.js" type="text/javascript"></script>
    <script src="js/ng/ng.cabinet.js" type="text/javascript"></script>
    <script>
        Tablesaw = {
            i18n: {
                modes: [ 'Stack', 'Swipe', 'Toggle' ],
                columns: 'Кол<span class=\"a11y-sm\">онк</span>и',
                columnBtnText: 'Колонки',
                columnsDialogError: 'Колонки недоступні.',
                sort: 'Сортування'
            }
        };
    </script>
    <%--<link rel="stylesheet" href="css/ng/cabinet.css" />--%>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp" flush="true">
    <jsp:param name="activeTab" value=""/>
</jsp:include>

<div class="container-fluid page-content">
    <div class="container pad-container">
        <article>
            <div id="message-container"></div>
            <h2><%=bundle.getString("cabinet.personal_cabinet")%></h2>
            <div class="user-board" style="width: 100%;">
                <div class="profile-menu">
                    <ul class="profile-menu-list">
                        <li class="profile-menu-list__item green">
                            <a href="#" id="order-history" class="profile-menu-list__item_text">Історія замовлень</a>
                        </li>
                        <li class="profile-menu-list__item yellow">
                            <a href="#" id="password-change" class="profile-menu-list__item_text">Зміна паролю</a>
                        </li>
                        <li class="profile-menu-list__item red">
                            <a href="#" id="personal-data" class="profile-menu-list__item_text">Особисті дані</a>
                        </li>
                        <li class="profile-menu-list__item blue">
                            <a href="#" id="cabinet-logout" class="profile-menu-list__item_text">Вихід</a>
                        </li>
                    </ul>
                </div>
                <div class="profile-workplace">
                    <div class="history-table">
                        <table class="tablesaw tablesaw-swipe tablesaw-sortable" data-tablesaw-mode-exclude="columntoggle" data-tablesaw-mode="swipe" data-tablesaw-sortable="" data-tablesaw-sortable-switch="" data-tablesaw-minimap="" data-tablesaw-mode-switch="">
                            <thead>
                            <tr>
                                <th data-tablesaw-sortable-col data-tablesaw-sortable-switch data-tablesaw-priority="persist"><%=bundle.getString("cabinet.passenger")%></th>
                                <th data-tablesaw-priority="6"><%=bundle.getString("cabinet.phones")%></th>
                                <th data-tablesaw-sortable-col data-tablesaw-sortable-switch data-tablesaw-priority="4"><%=bundle.getString("cabinet.route")%></th>
                                <th data-tablesaw-sortable-col data-tablesaw-sortable-switch data-tablesaw-priority="4"><%=bundle.getString("cabinet.departure_date")%></th>
                                <th data-tablesaw-priority="2"><%=bundle.getString("cabinet.seat")%></th>
                                <th data-tablesaw-sortable-col data-tablesaw-sortable-switch data-tablesaw-priority="6"><%=bundle.getString("cabinet.status")%></th>
                            </tr>
                            </thead>
                            <tbody>

                            </tbody>
                        </table>
                        <div class="tablesaw-pager"></div>
                    </div>
                </div>

            <%--<div id="accordion" style="display: none;">--%>
                <%--<h3><%=bundle.getString("cabinet.order_history")%></h3>--%>
                <%--<div>--%>
                    <%--<table class="table table-bordered table-striped table-hover table-tickets" style=" margin-bottom: 0px;">--%>
                        <%--<thead>--%>
                            <%--<tr>--%>
                                <%--<th><%=bundle.getString("cabinet.passenger")%></th>--%>
                                <%--<th><%=bundle.getString("cabinet.phones")%></th>--%>
                                <%--<th><%=bundle.getString("cabinet.route")%></th>--%>
                                <%--<th><%=bundle.getString("cabinet.departure_date")%></th>--%>
                                <%--<th><%=bundle.getString("cabinet.seat")%></th>--%>
                                <%--<th><%=bundle.getString("cabinet.status")%></th>--%>
                                <%--&lt;%&ndash;<th><%=bundle.getString("cabinet.actions")%></th>&ndash;%&gt;--%>
                            <%--</tr>--%>
                        <%--</thead>--%>
                        <%--<tbody></tbody>--%>
                    <%--</table>--%>
                    <%--<div class="pager"><a id="prev-page" class="pager-control" href="#">&lt;</a><span class="page-count">1</span><a id="next-page" class="pager-control" href="#">&gt;</a></div>--%>
                <%--</div>--%>
                <%--<h3><%=bundle.getString("cabinet.personal_data")%></h3>--%>
                <%--<div>--%>
                    <%--<p>--%>
                    <%--</p>--%>
                <%--</div>--%>
                <%--<h3><%=bundle.getString("cabinet.change_password")%></h3>--%>
                <%--<div>--%>
                    <%--<div id="change-pass" class="forms">--%>
                        <%--<ul class="forms-inline-list">--%>
                            <%--<li>--%>
                                <%--<label>--%>
                                    <%--<%=bundle.getString("cabinet.old_password")%>--%>
                                    <%--<input type="password" id="user-password-old" name="user-password-old" class="width-50"/>--%>
                                <%--</label>--%>
                            <%--</li>--%>
                            <%--<li>--%>
                                <%--<label>--%>
                                    <%--<%=bundle.getString("cabinet.new_one")%>--%>
                                    <%--<input type="password" id="user-password" name="user-password" class="width-50"/>--%>
                                <%--</label>--%>
                            <%--</li>--%>
                        <%--</ul>--%>
                        <%--<p><button class="btn btn-green light"><%=bundle.getString("cabinet.change_it")%></button></p>--%>
                    <%--</div>--%>
                <%--</div>--%>
            </div>
        </article>
    </div>
</div>


<jsp:include page="/WEB-INF/jsp/footer.jsp" flush="true"/>
</body>
</html>
