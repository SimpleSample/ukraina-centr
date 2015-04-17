package com.nagornyi.uc.appinfo;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Nagornyi
 * Date: 08.06.14
 */
public class AppInfoManager {
    private static Logger log = Logger.getLogger(AppInfoManager.class.getName());

    AppInfo appInfo = new AppInfo();
    private Map<String, AppInfoLoader> loaders = new HashMap<String, AppInfoLoader>();

    public void loadInfo(AppInfoLoader loader) {
        Map<String, Object> info = new HashMap<String, Object>();
        try {
            log.info("Loading app info with loader " + loader.getClass().getName());
            loader.load(info);
        } catch (AppInfoLoadException e) {
            log.log(Level.WARNING, "Couldn't load app info with loader " + loader.getClass().getName(), e);
        }
        Double currencyRate = (Double)info.get("currencyRate");
        if (currencyRate != null) {
            appInfo.setCurrencyRate(currencyRate);
        }
    }

    public void loadInfo(String loaderName) {
        AppInfoLoader loader = loaders.get(loaderName);
        if (loader == null) throw new RuntimeException("App Info Loader " + loaderName + " wasn't found");

        loadInfo(loader);
    }

    public void registerLoader(String name, AppInfoLoader loader) {
        loaders.put(name, loader);
    }


    private static final AppInfoManager INSTANCE = new AppInfoManager();

    public static AppInfoManager getInstance() {
        return INSTANCE;
    }

    public double getCurrencyRate() {
        return appInfo.getCurrencyRate();
    }
}
