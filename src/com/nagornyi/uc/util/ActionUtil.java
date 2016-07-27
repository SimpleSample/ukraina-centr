package com.nagornyi.uc.util;

import com.google.appengine.repackaged.org.codehaus.jackson.map.ObjectMapper;
import com.nagornyi.uc.transport.ActionRequest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Nagornyi
 * Date: 09.06.14
 */
public class ActionUtil {

    private static Logger log = Logger.getLogger(ActionUtil.class.getName());

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String serializeObject(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not serialize object", e);
        }
        return null;
    }

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
