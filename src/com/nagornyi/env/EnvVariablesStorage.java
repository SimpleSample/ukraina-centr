package com.nagornyi.env;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.api.ApiProxy;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class EnvVariablesStorage {
    private static final Logger log = Logger.getLogger(EnvVariablesStorage.class.getName());

    private static Map<String, Object> vars = new ConcurrentHashMap<>();

    public static void loadEnvVariables() {
        String appId = ApiProxy.getCurrentEnvironment().getAppId().replaceAll("~", "");
        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
            appId = "sour-little-baby";
        }
        log.info("App id: " + appId);
        InputStream stream = EnvVariablesStorage.class.getResourceAsStream("secrets/"+appId+"/env_parameters.json");
        try {
            vars = JacksonFactory.getDefaultInstance().createJsonParser(stream).parse(HashMap.class);
            log.info("Loaded environment parameters: " + vars);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load env parameters from file");
        }
    }

    public static String getValue(String group, String key) {
        if (!vars.containsKey(group) || !(vars.get(group) instanceof Map)) {
            return null;
        }

        Map<String, String> groupValues = (Map)vars.get(group);
        return groupValues.get(key);
    }
}
