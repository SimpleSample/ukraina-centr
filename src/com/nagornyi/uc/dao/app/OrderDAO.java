package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.nagornyi.uc.dao.IOrderDAO;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Ticket;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Order findByExternalId(Long externalId) {
        List<Order> orders = getByProperty("externalId", externalId);
        if (CollectionUtils.isEmpty(orders)) {
            throw new NullPointerException("No order was found for external id " + externalId);
        }
        return orders.iterator().next();
    }

    @Override
    public List<Order> getOrdersForTickets(List<Ticket> tickets) {
        Map<Key, Order> ordersMap = new HashMap<>();
        for (Ticket ticket: tickets) {
            Key orderId = ticket.getOrderId();
            if (orderId != null && !ordersMap.containsKey(orderId)) {
                ordersMap.put(orderId, ticket.getOrder());
            }
        }
        return new ArrayList<>(ordersMap.values());
    }

    @Override
    public int sanitizeOrders(List<Order> orderList) {
        log.info("Orders count: " + orderList.size());

        List<Order> ordersToDelete = new ArrayList<>();
        for (Order order: orderList) {
            if (CollectionUtils.isEmpty(order.getTickets())) {
                ordersToDelete.add(order);
            }
        }
        log.info("Deleting Orders count: " + ordersToDelete.size());
        logOrdersExternalIds(ordersToDelete);

        delete(ordersToDelete);

        return ordersToDelete.size();
    }

    private void logOrdersExternalIds(List<Order> orderList) {
        List<Long> externalIds = new ArrayList<>(orderList.size());

        for(Order order: orderList) {
            externalIds.add(order.getExternalId());
        }

        log.info("Orders external ids: " + externalIds);
    }
}
