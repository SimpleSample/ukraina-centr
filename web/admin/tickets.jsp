<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/admin/imports.jsp" flush="true"/>
    <link href="../admin/css/adminpage.tickets.css" rel="stylesheet">
</head>
<body class="admin-tickets-page">
<jsp:include page="/WEB-INF/jsp/admin/header.jsp" flush="true"/>
<div class="container-fluid">
    <div class="row">
        <jsp:include page="/WEB-INF/jsp/admin/sidebar.jsp" flush="true"/>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <div class="dropdown header-dropdown">
                <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                    <span>Рейс - </span><span class="dropdown-value"></span>
                    <span class="glyphicon glyphicon-menu-down" aria-hidden="true"></span>
                </button>
                <ul class="dropdown-menu" aria-labelledby="dropdownMenu1">
                </ul>
            </div>


            <div class="row placeholders">
                <div class="tickets-toolbar">
                    <button id="save-button" type="button" class="btn btn-primary">
                        <span class="glyphicon glyphicon-floppy-save" aria-hidden="true"></span>Зберегти зміни</button>

                    <button id="get-contacts" type="button" class="btn btn-default">
                        <span class="glyphicon glyphicon-import" aria-hidden="true"></span>Завантажити Контакти з Google</button>
                </div>
                <div class="tickets-workspace container-fluid">
                    <div id="available-seats" class="col-lg-8 col-md-6">

                        <jsp:include page="/WEB-INF/jsp/bus-template.jsp" flush="true"/>
                    </div>
                    <div id="ticket-detailed-info" class="col-lg-4 col-md-6">
                        <div class="container-fluid input-groups">
                            <div><h3>Пасажир</h3></div>
                            <div class="input-group">
                                <span class="input-group-addon" id="basic-addon1">@</span>
                                <input id="name-substring-search" type="text" class="form-control" placeholder="Імя" aria-describedby="basic-addon1">
                                <div class="dropdown name-substring-search-dropdown">
                                    <ul class="dropdown-menu">
                                    </ul>
                                </div>
                            </div>
                            <div class="input-group">
                                <span class="input-group-addon" id="basic-addon2"><span class="glyphicon glyphicon-phone-alt" aria-hidden="true"></span></span>
                                <input id="passenger-phone" type="text" class="form-control" placeholder="Телефон" aria-describedby="basic-addon2">
                            </div>
                            <div class="dropdown dropdown-discounts">
                                <button class="btn btn-default dropdown-toggle" type="button" id="discount-menu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                                    <span class="dropdown-value" data-selectedId="discount-NONE">Без знижки</span>
                                    <span class="glyphicon glyphicon-menu-down" aria-hidden="true"></span>
                                </button>
                                <ul class="dropdown-menu" aria-labelledby="discount-menu">
                                    <li><a href="#" id="discount-BABY">Дитина до 4-х років</a></li>
                                    <li><a href="#" id="discount-CHILD">Дитина до 12-ти років</a></li>
                                    <li><a href="#" id="discount-STUDENT">Студент</a></li>
                                    <li><a href="#" id="discount-OLDMAN">Пенсіонер</a></li>
                                    <li><a href="#" id="discount-FORSIX">Член групи з 6-ти чол.</a></li>
                                    <li><a href="#" id="discount-NONE">Без знижок</a></li>
                                </ul>
                            </div>
                            <div class="textarea-group">
                                <textarea id="passenger-note" class="form-control" rows="3" placeholder="Примітка"></textarea>
                            </div>
                            <h3>Вартість квитка <span id="active-ticket-price" class="label label-default"></span></h3>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/admin/lateImports.jsp" flush="true"/>
<script src="${pageContext.request.contextPath}/admin/js/adminpage.topdropdown.js"type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/admin/js/adminpage.tickets.js" type="text/javascript"></script>
</body>
</html>
