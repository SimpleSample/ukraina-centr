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
    private Long externalId;

    public Order(Entity entity) {
        super(entity);
    }

    public Order(User user) {
        super(user.getKey());
        setStatus(Status.PROCESSING);
        Date date = new Date();
        setStatusChangedDate(date);
        setProperty("externalId", date.getTime());
    }

    public List<Ticket> getTickets() {
        return DAOFacade.getDAO(Ticket.class).getByProperty("orderId", getKey());
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

    public void succeeded() {
        setStatus(Status.SUCCESS);
        List<Ticket> tickets = getTickets();
        for (Ticket ticket: tickets) {
            ticket.setStatus(Ticket.Status.RESERVED);
        }
        DAOFacade.bulkSave(tickets);
        DAOFacade.save(this);
    }

    public void processing() {
        setStatus(Status.PROCESSING);
        List<Ticket> tickets = getTickets();
        for (Ticket ticket: tickets) {
            ticket.setStatus(Ticket.Status.PROCESSING);
        }
        DAOFacade.bulkSave(tickets);
        DAOFacade.save(this);
    }

    public void failed() {
        setStatus(Order.Status.FAILURE);
        List<Ticket> tickets = getTickets();
        for(Ticket ticket: tickets) {
            ticket.setStatus(Ticket.Status.INVALID);
        }
        DAOFacade.bulkSave(tickets);
        DAOFacade.save(this);
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

    public Long getExternalId() {
        return getProperty("externalId");
    }

    public enum Status {
        PROCESSING,
        SUCCESS,
        FAILURE
    }
}
