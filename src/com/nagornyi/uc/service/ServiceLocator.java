package com.nagornyi.uc.service;

/**
 * Created by artemnagorny on 30.08.15.
 * TODO DI
 */
public class ServiceLocator {
    private static final ServiceLocator INSTANCE = new ServiceLocator();

    private OrderService orderService = new OrderService();
    private PriceService priceService = new PriceService();
    private UserService userService = new UserService();
    private TicketService ticketService = new TicketService();

    public static ServiceLocator getInstance() {
        return INSTANCE;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public PriceService getPriceService() {
        return priceService;
    }

    public UserService getUserService() {
        return userService;
    }

    public TicketService getTicketService() {
        return ticketService;
    }
}
