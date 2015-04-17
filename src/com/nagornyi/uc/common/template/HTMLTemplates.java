package com.nagornyi.uc.common.template;

import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.nagornyi.uc.common.DateFormatter;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.templates.TemplatesManager;

import java.util.List;
import java.util.Locale;

/**
 * Created by Yanko on 30.05.14.
 */
public class HTMLTemplates {

    private static final String userOrderTemplate0 = "<table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"640\">\n" +
            "    <tbody>\n" +
            "    <tr>\n" +
            "        <td align=\"left\" style=\"line-height:23px;font-size:1px\" valign=\"top\">&nbsp;</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "        <td align=\"left\" style=\"font-size:20px;line-height:28px;font-family:'Segoe UI','Segoe WP','Segoe UI Regular','Helvetica Neue',Helvetica,Tahoma,'Arial Unicode MS',Sans-serif;color:#454546\" valign=\"top\">\n" +
            "            <h1 style=\"font-family:'Segoe UI','Segoe WP','Segoe UI Regular','Helvetica Neue',Helvetica,Tahoma,'Arial Unicode MS',Sans-serif;font-size:30px;line-height:38px;color:#454546;font-weight:normal;\">Вітаємо Вас";

    private static String userOrderTemplate1 = "!</h1>Ви вдало забронювали ";

    private static final String userOrderTemplate2 = " на сайті <a href=\"www.ukraina-centr.com\" style=\"color: #8BD61A; text-decoration: none;\">www.ukraina-centr.com</a> Деталі бронювань:\n" +
            "        </td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "        <td align=\"left\" style=\"line-height:23px;font-size:1px\" valign=\"top\">&nbsp;</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "        <td bgcolor=\"#ffffff\">\n" +
            "            <table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"float:left;border:1px solid #ffffff; font-family:'Segoe UI','Segoe WP','Segoe UI Regular','Helvetica Neue',Helvetica,Tahoma,'Arial Unicode MS',Sans-serif;color:#454546;\" width=\"640\">\n" +
            "                <tbody>\n" +
            "                <tr>\n" +
            "                    <td align=\"center\" style=\"font-size:20px;line-height:48px;border-bottom: 3px solid #EEEBEB;\" valign=\"middle\"><b>Пасажир</b>\n" +
            "                    </td>\n" +
            "                    <td align=\"center\" style=\"font-size:20px;line-height:48px;border-bottom: 3px solid #EEEBEB;\" valign=\"middle\"><b>Маршрут</b>\n" +
            "                    </td>\n" +
            "                    <td align=\"center\" style=\"font-size:20px;line-height:48px;border-bottom: 3px solid #EEEBEB;\" valign=\"middle\"><b>Дата відправлення</b>\n" +
            "                    </td>\n" +
            "                    <td align=\"center\" style=\"font-size:20px;line-height:48px;border-bottom: 3px solid #EEEBEB;\" valign=\"middle\"><b>Місце</b>\n" +
            "                    </td>\n" +
            "                </tr>";

    private static final String userOrderTemplate3 = "</tbody>\n" +
            "            </table>\n" +
            "        </td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "        <td align=\"left\" style=\"line-height:23px;font-size:1px\" valign=\"top\">&nbsp;</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "        <td bgcolor=\"#ffffff\">\n" +
            "    <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:1px solid #ffffff;\">\n" +
            "        <tbody>\n" +
            "        <tr>\n" +
            "        <td align=\"left\" style=\"padding-right:10px;line-height:28px;font-size:16px;font-family:'Segoe UI','Segoe WP','Segoe UI Regular','Helvetica Neue',Helvetica,Tahoma,'Arial Unicode MS',Sans-serif;color:#454546;\" valign=\"bottom\">Ціна квитків:</td>\n" +
            "        <td align=\"left\" style=\"line-height:38px;font-size:34px;font-family:'Segoe UI','Segoe WP','Segoe UI Regular','Helvetica Neue',Helvetica,Tahoma,'Arial Unicode MS',Sans-serif;color:#454546;\" valign=\"top\"><b>";

    private static final String userOrderTemplate4 = "₴</b></td>\n" +
            "        </tr>\n" +
            "        </tbody>\n" +
            "    </table>\n" +
            "        </td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "        <td align=\"left\" style=\"line-height:23px;font-size:1px\" valign=\"top\">&nbsp;</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "        <td align=\"left\" style=\"line-height:23px;font-size:1px\" valign=\"top\">&nbsp;</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "    <td align=\"left\" style=\"font-size:24px;line-height:28px;font-family:'Segoe UI','Segoe WP','Segoe UI Regular','Helvetica Neue',Helvetica,Tahoma,'Arial Unicode MS',Sans-serif;color:#454546\" valign=\"top\">\n" +
            "        Дякуємо Вам за довіру та приємних подорожей з <a href=\"www.ukraina-centr.com\" style=\"color: #8BD61A; text-decoration: none;\">www.ukraina-centr.com</a>\n" +
            "    </td>\n" +
            "    </tr>\n" +
            "    </tbody>\n" +
            "</table>";

    private static String td_template = "<td align=\"center\" style=\"font-size:16px;line-height:38px;border-bottom: 1px solid #EEEBEB;\" valign=\"middle\">";
    private static String td_template_end = "</td>";


    public static String getUserReservationTemplate(double eurSummaryPrice, double summaryPrice, List<Ticket> tickets, User user) {
        System.out.println("summary price " + summaryPrice);
        Locale locale = user.getUserLocale();
        SoyListData ticketsSoy = new SoyListData();
        for (Ticket ticket: tickets) {
            SoyMapData t = new SoyMapData();
            t.put("passenger", ticket.getPassenger());
            t.put("route", ticket.getStartCity().getLocalizedName(locale) + " - " + ticket.getEndCity().getLocalizedName(locale));
            t.put("startDate", DateFormatter.format(ticket.getStartDate(), locale));
            t.put("seat", ticket.getSeat().getSeatNum());
            ticketsSoy.add(t);
        }

        SoyMapData data = new SoyMapData();
        data.put("username", user.getName() + " " +user.getSurname());
        data.put("tickets", ticketsSoy);
        data.put("price", eurSummaryPrice);
        data.put("realPrice", summaryPrice);
        data.put("isPartner", user.isPartner());
        return TemplatesManager.INSTANCE.renderTemplate("templates.reserve", data);
//        String ticketWord = (tickets.size()==1? " квиток":(tickets.size() > 1 && tickets.size()<5? " квитка" : " квитків"));
//
//       StringBuilder builder = new StringBuilder(userOrderTemplate0);
//        if (user.isPartner()) {
//            builder.append(userOrderTemplate1);
//        } else {
//            builder.append(", ").append(user.getName()).append(" ").append(user.getSurname()).append(userOrderTemplate1);
//        }
//        builder.append(tickets.size()).append(ticketWord).append(userOrderTemplate2);
//        Locale locale = user.getUserLocale();
//        for(Ticket ticket: tickets) {
//            builder.append("<tr>");
//            builder.append(td_template).append(ticket.getPassenger()).append(td_template_end);
//            builder.append(td_template).append(ticket.getStartCity().getLocalizedName(locale)).append(" - ").append(ticket.getEndCity().getLocalizedName(locale)).append(td_template_end);
//            builder.append(td_template).append(DateFormatter.format(ticket.getStartDate(), locale)).append(td_template_end);
//            builder.append(td_template).append(ticket.getSeat().getSeatNum()).append(td_template_end);
//            builder.append("</tr>");
//        }
//
//        builder.append(userOrderTemplate3).append(eurSummaryPrice).append("€ <span style=\"font-weight:100;\">або</span> ").append(summaryPrice).append(userOrderTemplate4);
//
//        return builder.toString();
    }
}
