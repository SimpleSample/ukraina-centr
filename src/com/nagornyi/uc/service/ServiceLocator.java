package com.nagornyi.uc.service;

/**
 * Created by artemnagorny on 30.08.15.
 * TODO DI
 */
public class ServiceLocator {
    private static final ServiceLocator INSTANCE = new ServiceLocator();

    private OrderService orderService = new OrderService();
    private PriceService priceService = new PriceService();

    public static ServiceLocator getInstance() {
        return INSTANCE;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public PriceService getPriceService() {
        return priceService;
    }
}
