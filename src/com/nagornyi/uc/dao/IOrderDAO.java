package com.nagornyi.uc.dao;

import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Ticket;

import java.util.List;

/**
 * @author Nagornyi
 * Date: 17.06.14
 */
public interface IOrderDAO extends DAO<Order> {

    Order findByExternalId(Long externalId);

    int sanitizeOrders(List<Order> orders);

    List<Order> getOrdersForTickets(List<Ticket> tickets);

}
