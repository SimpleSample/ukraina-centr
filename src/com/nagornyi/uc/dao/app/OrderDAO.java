package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Entity;
import com.nagornyi.uc.dao.IOrderDAO;
import com.nagornyi.uc.entity.Order;

/**
 * @author Nagornyi
 * Date: 17.06.14
 */
public class OrderDAO extends EntityDAO<Order> implements IOrderDAO {

    @Override
    protected Order createDAOEntity(Entity entity) {
        return new Order(entity);
    }

    @Override
    protected String getKind() {
        return Order.class.getSimpleName();
    }

    @Override
    public void cancelOrder(String orderId) {

    }
}
