package com.nagornyi.uc.dao;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.nagornyi.uc.entity.BaseEntity;

import java.util.List;
import java.util.Set;

/**
 * @author Nagorny
 *         Date: 12.05.14
 */
public interface DAO<Entity extends BaseEntity> {

    Entity getById(Key id);

    Entity getByKey(Key key);

    List<Entity> getByProperty(String prop, Object propValue);

    List<Entity> getAll();

    List<Entity> getByParent(Key parentKey);

    Key create(Entity wrapper);

    List<Key> create(List<Entity> wrappers);

    Key save(Entity wrapper);

    List<Key> save(List<Entity> wrappers);

    void delete(Entity entity);

    Set<Key> deleteAll();

    Set<Key> deleteForQuery(Query query);
}
