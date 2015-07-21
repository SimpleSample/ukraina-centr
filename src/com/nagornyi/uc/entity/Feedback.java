package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;

public class Feedback extends EntityWrapper {

    private String username;
    private Long date;
    private String feedback;

    public Feedback() {
    }

    public Feedback(Entity entity) {
        super(entity);
    }

    public String getUsername() {
        return getProperty("username");
    }

    public void setUsername(String username) {
        this.setProperty("username", username);
    }

    public Long getDate() {
        return getProperty("date");
    }

    public void setDate(Long date) {
        this.setProperty("date", date);
    }

    public String getFeedback() {
        return getProperty("feedback");
    }

    public void setFeedback(String feedback) {
        this.setProperty("feedback", feedback);
    }
}
