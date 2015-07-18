package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * @author Nagorny
 *         Date: 25.04.14
 */
public abstract class EntityWrapper implements BaseEntity {
    private Entity entity;
    private String stringKey;
    private Key key;
    private String stringParentKey;
    private Key parentKey;

    public EntityWrapper() {
        entity = new Entity(getClass().getSimpleName());
    }

    public EntityWrapper(String id) {
        entity = new Entity(getClass().getSimpleName(), id);
    }

    public EntityWrapper(String id, Key parentKey) {
        entity = new Entity(getClass().getSimpleName(), id, parentKey);
    }

    public EntityWrapper(Key parentKey) {
        entity = new Entity(getClass().getSimpleName(), parentKey);
    }

    public EntityWrapper(Entity entity) {
        this.entity = entity;
    }

    protected void setProperty(String propertyName, Object value) {
        entity.setProperty(propertyName, value);
    }

    protected <T> T getProperty(String propertyName) {
        return (T)entity.getProperty(propertyName);
    }

	protected Integer getIntProperty(String propertyName) {
		Object s = getProperty(propertyName); //TODO weird
		if (s instanceof Long) {
			return (int)((Long) s).longValue();
		}
		return (Integer)s;
	}

    public Entity getEntity() {
        return entity;
    }

    public Key getKey() {
        if (key == null) fillKey();
        return key;
    }

    public Key getParentKey() {
        if (parentKey == null) fillParentKey();
        return parentKey;
    }

    public String getStringKey() {
        if (stringKey == null) fillKey();

        return stringKey;
    }

    public String getStringParentKey() {
        if (stringParentKey == null) fillParentKey();

        return stringParentKey;
    }

    private void fillKey() {
        key = getEntity().getKey();
        stringKey = KeyFactory.keyToString(key);
    }

    private void fillParentKey() {
        parentKey = getEntity().getParent();
        stringParentKey = KeyFactory.keyToString(parentKey);
    }

    public void flush() {

    }
}
