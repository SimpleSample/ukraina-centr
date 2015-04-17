package com.nagornyi.uc.appinfo;

import java.util.Map;

/**
 * @author Nagornyi
 * Date: 29.06.14
 */
public interface AppInfoLoader {

    void load(Map<String, Object> map) throws AppInfoLoadException;
}
