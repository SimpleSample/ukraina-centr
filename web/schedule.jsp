<%@ page import="java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    ResourceBundle bundle = ResourceBundle.getBundle("i18n.schedule", Locale.forLanguageTag("uk")); //TODO localization issue
%>
<!DOCTYPE html>
<html>
<head>
    <title><%=bundle.getString("schedule.schedule")%></title>
    <jsp:include page="/WEB-INF/jsp/imports.jsp" flush="true"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/header.jsp" flush="true">
    <jsp:param name="activeTab" value="schedule"/>
</jsp:include>

<div class="container-fluid page-content">
    <div class="container pad-container">
        <article>
            <h2>Кіровоград-Рим</h2>
            <ul class="list-1">
                <li>Відправлення з Кіровограду здійснюється кожен четвер об 11:30 з автовокзалу</li>
                <li>Відправлення з Риму - щонеділі об 11:15 з автостанції Тібуртіна</li>
            </ul>
            <h3>Детальний розклад</h3>
            <table class="table table-bordered table-striped table-hover" style=" margin-bottom: 0px;">
                <tbody>
                <tr>
                    <td>11:30, <i>четвер</i></td>
                    <td><b>Кіровоград</b> (АС-1, Олександрійське шосе, 8)</td>
                    <td>06:00, <i>вівторок</i></td>
                </tr>
                <tr>
                    <td>13:30, <i>четвер</i></td>
                    <td><b>Черкаси</b> (автовокзал, вул. Смілянська, 166а)</td>
                    <td>04:15, <i>вівторок</i></td>
                </tr>
                <tr>
                    <td>17:15, <i>четвер</i></td>
                    <td><b>Київ</b> (АС "Дачна", проспект Перемоги, 142)</td>
                    <td>00:30, <i>вівторок</i></td>
                </tr>
                <tr>
                    <td>19:15, <i>четвер</i></td>
                    <td><b>Житомир</b> (автовокзал, вул. Київська, 93)</td>
                    <td>22:30, <i>понеділок</i></td>
                </tr>
                <tr>
                    <td>22:00, <i>четвер</i></td>
                    <td><b>Рівне</b> (автовокзал, вул. Київська, 40)</td>
                    <td>19:45, <i>понеділок</i></td>
                </tr>
                <tr>
                    <td>01:30, <i>п'ятниця</i></td>
                    <td><b>Львів</b> (автовокзал, вул. Стрийська, 109)</td>
                    <td>16:15, <i>понеділок</i></td>
                </tr>
                <tr>
                    <td>05:30, <i>п'ятниця</i></td>
                    <td><b>Чоп / Zagony</b> (UA/H)</td>
                    <td>12:00, <i>понеділок</i></td>
                </tr>
                <tr>
                    <td>13:30, <i>п'ятниця</i></td>
                    <td><b>Tornyiszentmiklós / Pince</b> (H/SLO)</td>
                    <td>02:00, <i>понеділок</i></td>
                </tr>
                <tr>
                    <td>17:15, <i>п'ятниця</i></td>
                    <td><b>Fernetiči / Fernetti</b> (SLO/І)</td>
                    <td>22:15, <i>неділя</i></td>
                </tr>
                <tr>
                    <td>19:15, <i>п'ятниця</i></td>
                    <td><b>Mestre</b> (Viale Stazione)</td>
                    <td>20:30, <i>неділя</i></td>
                </tr>
                <tr>
                    <td>22:00, <i>п'ятниця</i></td>
                    <td><b>Cesena</b> (Piazzale Karl Marx)</td>
                    <td>17:45, <i>неділя</i></td>
                </tr>
                <tr>
                    <td>00:30, <i>субота</i></td>
                    <td><b>Perugia</b> (Piazza Vittorio Venetto)</td>
                    <td>15:15, <i>неділя</i></td>
                </tr>
                <tr>
                    <td>01:30, <i>субота</i></td>
                    <td><b>Foligno</b> (Piazza della Stazzione)</td>
                    <td>14:15, <i>неділя</i></td>
                </tr>
                <tr>
                    <td>02:45, <i>субота</i></td>
                    <td><b>Terni</b> (Piazza della Stazzione)</td>
                    <td>13:00, <i>неділя</i></td>
                </tr>
                <tr>
                    <td>04:00, <i>субота</i></td>
                    <td><b>Roma</b> (autostazione Tiburtina)</td>
                    <td>11:15, <i>неділя</i></td>
                </tr>
                </tbody>
            </table>
        </article>
    </div>
</div>

<jsp:include page="/WEB-INF/jsp/footer.jsp" flush="true"/>
</body>
</html>
