package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

/**
 * @author Nagorny
 *         Date: 25.04.14
 */
public class NamedEntity extends EntityWrapper {

    public NamedEntity() {
    }

    public NamedEntity(String id) {
        super(id);
    }

    public NamedEntity(Key parentKey) {
        super(parentKey);
    }

    public NamedEntity(Entity entity) {
        super(entity);
    }

    public NamedEntity(String id, Key parentKey) {
        super(id, parentKey);
    }

    public String getName() {
        return getProperty("name");
    }

    public void setName(String name) {
        setProperty("name", name);
    }
}
