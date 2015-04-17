<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.nagornyi.uc.dao.DAOFacade" %>
<%@ page import="com.nagornyi.uc.dao.ITripDAO" %>
<%@ page import="com.nagornyi.uc.entity.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.nagornyi.uc.common.DateFormatter" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="css/kube.css" type="text/css" media="screen">
    <link rel="stylesheet" href="css/admin.css" type="text/css" media="screen">
    <link rel="stylesheet" href="css/print-style.css" type="text/css" media="print" />
    <script src="../js/jquery-1.7.1.min.js" type="text/javascript"></script>
    <script src="../js/ng.js" type="text/javascript"></script>
    <script src="js/ng.admin.js" type="text/javascript"></script>
</head>
<body>
<%
    Route route = DAOFacade.findAll(Route.class).get(0);
    int period = 90; //days
    Calendar start = Calendar.getInstance();
    Calendar end = Calendar.getInstance();
    end.add(Calendar.DAY_OF_MONTH, period);
    ITripDAO dao = DAOFacade.getDAO(Trip.class);
    List<Trip> trips = dao.getTripsByDateRange(route, start.getTime(), end.getTime());
    City routeFirstCity = route.getFirstCity();
    City routeLastCity = route.getLastCity();
    Map<String, List<Ticket>> reservedTickets = new HashMap<String, List<Ticket>>();

%>
<div class="units-row">
    <div class="unit-centered passengers-page">
        <div class="forms show-trips no-print">
            <h2>Перегляд пасажирів на поїздку</h2>
            <label for="trips">
                <select id="trips" class="width-100">
                    <option></option>
                    <%
                        for (Trip trip : trips) {
                            String routeFirstCityStr = trip.isForth() ? routeFirstCity.getName() : routeLastCity.getName();
                            String routeLastCityStr = trip.isForth() ? routeLastCity.getName() : routeFirstCity.getName();
                            int allSeatsCount = trip.getSeatsNum();
                            List<Ticket> tickets = DAOFacade.findByParent(Ticket.class, trip.getEntity().getKey());
                            reservedTickets.put(trip.getStringKey(), tickets);
                            int reservedSeatsCount = tickets.size();
                            String startDate = DateFormatter.defaultFormat(trip.getStartDate());
                            String option = startDate + ", " + routeFirstCityStr + " - " + routeLastCityStr + ", " + reservedSeatsCount + "/" + allSeatsCount;
                    %>
                    <option id="<%=trip.getStringKey()%>"><%=option%>
                    </option>
                    <%
                        }
                    %>
                </select>

                <div class="forms-desc">Оберіть необхідний маршрут</div>
                <div class="btn-leftside">
                    <button id="search-tickets" class="btn btn-green">Пошук</button>
                </div>
            </label>
        </div>

        <table class="width-100">
            <caption>Результати пошуку</caption>
            <thead>
            <tr>
                <th></th>
                <th>Маршрут</th>
                <th>Телефони</th>
                <th>Пасажир</th>
                <th>Місце</th>
                <th>Оплата</th>
                <th>Примітка</th>
            </tr>
            </thead>
            <tbody id="tickets">
            </tbody>
        </table>
        <div class="btn-leftside no-print">
            <button id="print-results" class="btn btn-yellow" style="display: none;">Друк</button>
        </div>
    </div>
</div>
</body>
</html>
