package com.nagornyi.uc.util;

import com.nagornyi.uc.transport.ActionRequest;

/**
 * @author Nagornyi
 * Date: 09.06.14
 */
public class ActionUtil {

    public static void checkRequired(ActionRequest req, String... params) {
        for (String param: params) {
            if (req.getParam(param) == null) throw new NullPointerException("Action param " + param + " is null");
        }
    }

    public static void checkRequired(Object... params) {
        for (Object param: params) {
            if (param == null) throw new NullPointerException("One of action params is null");
        }
    }
}
