package com.nagornyi.uc.common.template;

import com.nagornyi.uc.entity.User;

/**
 * @author Nagornyi
 *         Date: 19.06.14
 */
public class FailedOrderTemplate {

    public static String getFailedReservation(User user) {
        return get(user, "забронювати");
    }

    public static String getFailedPurchase(User user) {
        return get(user, "придбати");
    }

    private static String get(User user, String orderType) {
        String name = user.getName();
        if (user.getSurname() != null) name += " " + user.getSurname();

        return "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"640\">\n" +
                "    <tbody>\n" +
                "    <tr>\n" +
                "        <td align=\"left\" style=\"line-height:23px;font-size:1px\" valign=\"top\">&nbsp;</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td align=\"left\" style=\"font-size:20px;line-height:28px;font-family:'Segoe UI','Segoe WP','Segoe UI Regular','Helvetica Neue',Helvetica,Tahoma,'Arial Unicode MS',Sans-serif;color:#454546\" valign=\"top\">\n" +
                "            <h1 style=\"font-family:'Segoe UI','Segoe WP','Segoe UI Regular','Helvetica Neue',Helvetica,Tahoma,'Arial Unicode MS',Sans-serif;font-size:30px;line-height:38px;color:#454546;font-weight:normal;padding:0px,50px,0px,0px\">Вітаємо Вас, "+name +"!</h1>На жаль, спроба "+orderType+" квитки на сайті <a href=\"http://www.ukraina-centr.com\" style=\"color:#8bd61a;text-decoration:none\" target=\"_blank\">www.ukraina-centr.com</a> закінчилась невдало. Будь ласка, відвідайте наш сайт знову та спробуйте ще раз!\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    \n" +
                "    <tr>\n" +
                "        <td align=\"left\" style=\"line-height:23px;font-size:1px\" valign=\"top\">&nbsp;</td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td align=\"left\" style=\"font-size:24px;line-height:28px;font-family:'Segoe UI','Segoe WP','Segoe UI Regular','Helvetica Neue',Helvetica,Tahoma,'Arial Unicode MS',Sans-serif;color:#454546\" valign=\"top\">\n" +
                "            Дякуємо Вам за довіру та приємних подорожей з <a href=\"http://www.ukraina-centr.com\" style=\"color:#8bd61a;text-decoration:none\" target=\"_blank\">www.ukraina-centr.com</a>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    </tbody>\n" +
                "</table>";
    }
}
