package com.nagornyi.uc.util;

import java.util.List;

/**
 * @author Nagornyi
 *         Date: 17.06.14
 */
public class StringUtils {

    public static String join(List<String> list, String separator) {
        if (list.isEmpty()) return "";

        String result = list.remove(0);
        for (String item: list) {
            result += separator + item;
        }
        return result;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.equals("");
    }
}
