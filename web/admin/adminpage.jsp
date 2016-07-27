<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/jsp/admin/imports.jsp" flush="true"/>
</head>
<body class="admin-overview-page">
    <jsp:include page="/WEB-INF/jsp/admin/header.jsp" flush="true"/>
    <div class="container-fluid">
        <div class="row">
            <jsp:include page="/WEB-INF/jsp/admin/sidebar.jsp" flush="true"/>
            <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
                <div class="dropdown header-dropdown">
                    <button class="btn btn-default dropdown-toggle dropdown-toggle-hidden" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                        <span>Найближчий рейс - </span><span class="dropdown-value"></span>
                        <span class="glyphicon glyphicon-menu-down" aria-hidden="true"></span>
                    </button>
                    <ul class="dropdown-menu" aria-labelledby="dropdownMenu1"></ul>
                </div>
                <div class="row placeholders">
                    <div id="column-chart-container" class="col-md-6">

                    </div>
                    <div id="pie-chart-container" class="col-md-6">

                    </div>
                </div>

                <h2 class="sub-header">Пасажири</h2>
                <div class="history-table">
                    <div class="clear-both">
                        <button id="print-table" type="button" style="float: left; margin-bottom: 10px;" class="btn btn-primary">
                            <span class="glyphicon glyphicon-print" aria-hidden="true"></span> Друк
                        </button>
                    </div>
                    <table class="tablesaw tablesaw-sortable" data-tablesaw-mode="stack" data-tablesaw-sortable="">
                        <thead>
                        <tr>
                            <th data-tablesaw-sortable-col data-tablesaw-sortable-switch>Пасажир</th>
                            <th>Телефони</th>
                            <th data-tablesaw-sortable-col data-tablesaw-sortable-switch>Маршрут</th>
                            <th data-tablesaw-sortable-col data-tablesaw-sortable-switch>Дата відправлення</th>
                            <th data-tablesaw-sortable-col data-tablesaw-sortable-switch>Місце</th>
                            <th data-tablesaw-sortable-col data-tablesaw-sortable-switch>Оплата</th>
                            <th>Примітка</th>
                            <th data-print-exclude>&nbsp;</th>
                        </tr>
                        </thead>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/jsp/admin/lateImports.jsp" flush="true"/>
    <script src="http://code.highcharts.com/highcharts.js"></script>
    <script src="${pageContext.request.contextPath}/js/ng/ng.printable.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/admin/js/adminpage.overview.js" type="text/javascript"></script>
</body>
</html>
