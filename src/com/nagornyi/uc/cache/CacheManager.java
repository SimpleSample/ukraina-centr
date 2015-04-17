package com.nagornyi.uc.cache;

import com.nagornyi.uc.entity.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Nagornyi
 * Date: 30.06.14
 */
public class CacheManager {
    private static Logger log = Logger.getLogger(CacheManager.class.getName());
    private static Map<Class<? extends EntityWrapper>, EntityCache> cacheMap = new HashMap<Class<? extends EntityWrapper>, EntityCache>();
    private static boolean initialized = false;

    static {
        cacheMap.put(Bus.class, new BusCache());
        cacheMap.put(City.class, new CityCache());
        cacheMap.put(Price.class, new PriceCache());
        cacheMap.put(Route.class, new RouteCache());
        cacheMap.put(Ticket.class, new TicketCache());
    }

    public static void initCache() {
        if (initialized) return;

        for (EntityCache cache: cacheMap.values()) {
            log.info("Initializing "+ cache.getClass().getSimpleName()+" cache");
            cache.fillCache();
        }
        initialized = true;
    }
}
