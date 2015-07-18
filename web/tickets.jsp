<%@ page import="java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    ResourceBundle bundle = ResourceBundle.getBundle("i18n.tickets", Locale.forLanguageTag("uk")); //TODO localization issue
%>
<html>
<head>
    <title><%=bundle.getString("tickets.tickets")%></title>
    <jsp:include page="/WEB-INF/jsp/imports.jsp" flush="true"/>
    <link rel="stylesheet" href="css/ng/tickets.css" type="text/css" media="screen">
    <link rel="stylesheet" href="css/pikaday.css" type="text/css" media="screen">
    <jsp:include page="/WEB-INF/jsp/i18n.jsp" flush="true">
        <jsp:param name="bundleName" value="client_tickets"/>
    </jsp:include>
    <script src="js/ng/ng.tickets.js" type="text/javascript"></script>
    <script defer src="js/pikaday.js" type="text/javascript"></script>
</head>
<body class="tickets-page">
    <jsp:include page="/WEB-INF/jsp/header.jsp" flush="true">
        <jsp:param name="activeTab" value="tickets"/>
    </jsp:include>

    <section id="content">
        <div class="main">
            <div class="container_12">
                <div class="wrapper">
                    <div class="center">
                        <article>
                            <div id="message-container"></div>
                            <div class="forms" style="display: block;">
                                <h3><%=bundle.getString("tickets.choose_trip")%></h3>

                                <div class="forms-inline-list">
                                    <table width="100%">
                                        <tbody>
                                        <tr>
                                            <td>
                                                <label>
                                                    <%=bundle.getString("tickets.from")%>
                                                    <select id="forth-city" class="width-50">
                                                        <option></option>
                                                    </select>
                                                </label>
                                            </td>
                                            <td>
                                                <label>
                                                    <%=bundle.getString("tickets.to")%>
                                                    <select id="back-city" class="width-50">
                                                        <option></option>
                                                    </select>
                                                </label>
                                            </td>
                                            <td>
                                                <label>
                                                    <%=bundle.getString("tickets.departure_date")%>
                                                    <input type="text" id="forth-date" name="forth-date" class="width-50"/>
                                                </label>
                                            </td>
                                            <td>
                                                <label>
                                                    <%=bundle.getString("tickets.arrival_date")%>
                                                    <input type="text" id="back-date" name="back-date" class="width-50"
                                                           disabled/>
                                                </label>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="both-checkbox">
                                                <input id="both-dirs" type="checkbox" name="checkbox-1">
                                                <label for="both-dirs"><%=bundle.getString("tickets.both_ways")%></label>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <hr id="division" style="display: none;">

                                <div class="order-board-wrapper">
                                    <div class="order-board clear-both" style="display: none;">
                                        <div class="results">
                                            <div id="forth-trips" style="display: none;">
                                            </div>
                                            <div id="back-trips" style="display: none;">
                                            </div>
                                        </div>
                                        <div class="ticket-board" style="display: none;">
                                            <div class="tickets-header clear-both">
                                                <div class="tickets-title">
                                                    <span><%=bundle.getString("tickets.tickets")%></span>
                                                </div>
                                                <!--<select class="change-currency">-->
                                                <!--<option id="uah" selected="">₴</option>-->
                                                <!--<option id="eur">€</option>-->
                                                <!--</select>-->
                                            </div>
                                            <div class="ticket-area">
                                                <div id="tickets" class="tickets"></div>
                                                <div class="button-area">
                                                    <button id="add-ticket" class="btn btn-green light"><%=bundle.getString("tickets.add")%></button>
                                                    <button id="delete-ticket" class="btn btn-green light"><%=bundle.getString("tickets.remove")%></button>
                                                    <button id="buy-tickets" class="btn btn-green light"><%=bundle.getString("tickets.buy")%></button>
                                                </div>
                                                <form method="POST" id="liq-form" action="https://www.liqpay.com/api/pay" accept-charset="utf-8">
                                                    <input type="hidden" name="public_key"/>
                                                    <input type="hidden" name="amount"/>
                                                    <input type="hidden" name="currency"/>
                                                    <input type="hidden" name="description"/>
                                                    <input type="hidden" name="order_id"/>
                                                    <input type="hidden" name="result_url"/>
                                                    <input type="hidden" name="server_url"/>
                                                    <input type="hidden" name="type" />
                                                    <input type="hidden" name="signature"/>
                                                    <input type="hidden" name="language"/>
                                                    <input type="hidden" name="sandbox"/>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <hr>
                                <button id="search-trips" class="btn btn-green light"><%=bundle.getString("tickets.search_trips")%></button>
                                <!--<button id="run" class="btn btn-green light">Run</button>-->
                            </div>
                        </article>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <jsp:include page="/WEB-INF/jsp/footer.jsp" flush="true"/>
</body>
</html>
