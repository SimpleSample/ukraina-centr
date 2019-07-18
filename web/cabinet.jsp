<%@ page import="java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.nagornyi.uc.i18n.I18n" %>
<%@ page  contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    ResourceBundle bundle = I18n.getBundle("i18n.cabinet", Locale.forLanguageTag("uk")); //TODO localization issue
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
    <script src="${pageContext.request.contextPath}/js/ng/ng.cabinet.contactinfo.js" type="text/javascript"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp" flush="true">
    <jsp:param name="activeTab" value=""/>
</jsp:include>

<div class="container-fluid page-content" style="min-height: 400px;">
    <div class="row page-content-wrapper">
        <div class="col-sm-3 col-md-2 profile-menu">
            <ul class="col-sm-3 col-md-2 profile-menu-list">
                <li id="order-history" class="profile-menu-list__item green active">
                    <span class="profile-menu-list__item_text"><%=bundle.getString("cabinet.order_history")%></span>
                </li>
                <li id="buy-ticket" class="profile-menu-list__item yellow">
                    <span class="profile-menu-list__item_text"><%=bundle.getString("cabinet.buy_ticket")%></span>
                </li>
                <li id="personal-data" class="profile-menu-list__item red">
                    <span class="profile-menu-list__item_text"><%=bundle.getString("cabinet.personal_data")%></span>
                </li>
                <li id="cabinet-logout" class="profile-menu-list__item blue">
                    <span class="profile-menu-list__item_text"><%=bundle.getString("cabinet.logout")%></span>
                </li>
            </ul>
        </div>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 user-board">
            <div id="message-container"></div>
            <h2><%=bundle.getString("cabinet.personal_cabinet")%></h2>
            <div id="order-history-block" class="profile-workplace">
                <div class="history-table">
                    <h3><%=bundle.getString("cabinet.order_history")%></h3>
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
            <div id="personal-info-block" class="profile-workplace container-fluid personal-info block-hidden" style="display: none;">
                <div class="contact-info-block">
                    <h3>Контактні дані</h3>
                    <form id="contact-data-form">
                        <div class="row">
                            <div class="area-block col-md-3 col-sm-4">
                                <label for="username-edit-field" class="area-label"><%=bundle.getString("cabinet.name")%></label>
                                <div><span class="area-value" id="username-field"></span>
                                    <input class="area-value-edit hidden" type="text" id="username-edit-field" name="username" required>
                                </div>
                            </div>
                            <div class="area-block col-md-3 col-sm-4">
                                <label for="surname-edit-field" class="area-label"><%=bundle.getString("cabinet.surname")%></label>
                                <div><span id="surname-field" class="area-value"></span>
                                    <input class="area-value-edit hidden" type="text" id="surname-edit-field" name="surname" required>
                                </div>
                            </div>
                            <div class="area-block col-md-3 col-sm-4">
                                <label for="language-edit-field" class="area-label"><%=bundle.getString("cabinet.language")%></label>
                                <div><span id="language-field" class="area-value"></span>
                                    <select id="language-edit-field" class="area-value-edit hidden">
                                        <option value="uk" selected>українська</option>
                                        <option value="ru">русский</option>
                                        <option value="en">english</option>
                                        <option value="it">italiano</option>
                                    </select>
                                </div>
                            </div>
                            <div class="area-block col-md-3 hidden-sm"></div>
                        </div>
                        <div class="row">
                            <div class="area-block col-md-6 col-sm-8">
                                <label class="area-label"><%=bundle.getString("cabinet.email")%></label>
                                <div><span id="email-field" class="area-value-visible"></span>
                                </div>
                            </div>
                            <div class="area-block col-md-3 col-sm-4">
                                <label for="phone-edit-field" class="area-label"><%=bundle.getString("cabinet.phone")%></label>
                                <div><span id="phone-field" class="area-value"></span>
                                    <input class="area-value-edit hidden" type="tel" id="phone-edit-field" name="phone">
                                </div>
                            </div>
                            <div class="area-block col-md-3 hidden-sm"></div>
                            <div class="area-block col-md-3 hidden-sm"></div>
                        </div>
                        <div>
                            <button type="button" id="edit-contact-data" class="btn btn-green"><%=bundle.getString("cabinet.edit")%></button>
                            <input type="submit" id="save-contact-data" class="btn btn-green hidden" value="<%=bundle.getString("cabinet.save")%>"/>
                            <button type="button" id="cancel-contact-data" class="btn hidden"><%=bundle.getString("cabinet.cancel")%></button>
                        </div>
                    </form>
                </div>
                <div class="password-change-block">
                    <h3>Зміна паролю</h3>
                    <div class="row">
                        <div class="area-block col-md-4 col-sm-6"><span class="area-label">Старий пароль</span><div><input id="user-password-old" type="password" class="area-value-edit"></div></div>
                        <div class="area-block col-md-4 col-sm-6"><span class="area-label">Новий пароль</span><div><input id="user-password" type="password" class="area-value-edit"></div></div>
                        <div class="area-block col-md-4 hidden-sm"></div>
                    </div>
                    <div><button id="password-change" class="btn btn-green">Змінити</button></div>
                </div>
            </div>
            <div class="feedback-circle"><span class="glyphicon glyphicon-bullhorn" title="Залишити відгук" aria-hidden="true"></span></div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/jsp/footer.jsp" flush="true"/>
</body>
</html>
