package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;
import com.nagornyi.uc.dao.DAOFacade;

import java.util.Date;
import java.util.List;

/**
 * @author Nagornyi
 * Date: 17.06.14
 */
public class Order extends EntityWrapper {
    public static final String TICKETS_DELIMITER = " ";

    private List<Ticket> tickets;
    private User user;
    private Status status;
    private String transactionId;
    private Date statusChangedDate;

    public Order(Entity entity) {
        super(entity);
    }

    public Order(User user) {
        super(user.getKey());
        setStatus(Status.PROCESSING);
        setStatusChangedDate(new Date());
    }

    public List<Ticket> getTickets() {
        if (tickets == null) {
            tickets = DAOFacade.getDAO(Ticket.class).getByProperty("orderId", getKey());
        }
        return tickets;
    }

    public User getUser() {
        return DAOFacade.findById(User.class, getParentKey());
    }

    public Status getStatus() {
        return Status.valueOf((String)getProperty("status"));
    }

    public void setStatus(Status status) {
        setProperty("status", status.name());
    }

    public String getTransactionId() {
        return getProperty("transactionId");
    }

    public void setTransactionId(String transactionId) {
        setProperty("transactionId", transactionId);
    }

    public Date getStatusChangedDate() {
        return getProperty("statusChangedDate");
    }

    public void setStatusChangedDate(Date statusChangedDate) {
        setProperty("statusChangedDate", statusChangedDate);
    }

    public enum Status {
        PROCESSING,
        SUCCESS,
        FAILURE
    }
}
