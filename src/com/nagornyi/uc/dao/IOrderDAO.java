package com.nagornyi.uc.dao;

import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Ticket;

/**
 * @author Nagornyi
 * Date: 17.06.14
 */
public interface IOrderDAO extends DAO<Order> {

    void cancelOrder(String orderId);
}
