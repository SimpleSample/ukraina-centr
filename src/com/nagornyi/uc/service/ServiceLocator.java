package com.nagornyi.uc.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by artemnagorny on 30.08.15.
 */
public class ServiceLocator {
    private static final ServiceLocator INSTANCE = new ServiceLocator();
    private Map<String, UCService> map = new ConcurrentHashMap<>();

    {
        map.put(OrderService.class.getSimpleName(), new OrderService());
        map.put(PriceService.class.getSimpleName(), new PriceService());
    }

    public static ServiceLocator getInstance() {
        return INSTANCE;
    }

    public <T extends UCService> T getService (String serviceName) {
        return (T)map.get(serviceName);
    }
}
