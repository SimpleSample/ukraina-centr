package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.*;
import com.nagornyi.uc.dao.DAO;
import com.nagornyi.uc.entity.EntityWrapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Nagorny
 * Date: 25.04.14
 */
public class EntityDAO<E extends EntityWrapper> implements DAO<E> {
    Logger log = Logger.getLogger(EntityDAO.class.getName());
    protected DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    protected E createDAOEntity(Entity entity) {return null;}

    protected String getKind() {return null;}

    @Override
    public Key create(EntityWrapper wrapper) {
        return datastore.put(wrapper.getEntity());
    }

    @Override
    public E getById(Key id) {
        Query q = new Query(getKind())
                .addFilter(Entity.KEY_RESERVED_PROPERTY,
                        Query.FilterOperator.EQUAL,
                        id);
        PreparedQuery pq = datastore.prepare(q);
        Entity ent = pq.asSingleEntity();
        return ent == null? null : createDAOEntity(ent);
    }

    protected List<E> getByFilter(Query.Filter filter) {
        return getByFilterAndSorting(filter, null, null);
    }

    protected List<E> getByFilterAndSorting(Query.Filter filter, String sortProp, Query.SortDirection dir) {
        return get(null, filter, sortProp, dir);
    }

    protected List<E> get(Key parent, Query.Filter filter, String sortProp, Query.SortDirection dir) {
        Query query = new Query(getKind()).setFilter(filter);
        if (sortProp != null) {
            query.addSort(sortProp, dir);
        }
        if (parent != null) {
            query.setAncestor(parent);
        }
        return getByQuery(query);
    }

    @Override
    public E getByKey(Key key) {
        Entity ent = null;
        try {
            ent = datastore.get(key);
        } catch (EntityNotFoundException e) {
            log.warning("No entity found for key " +key);
        }
        return ent == null? null : createDAOEntity(ent);
    }

    @Override
    public List<E> getByProperty(String prop, Object propValue) {
        Query q = new Query(getKind())
                .addFilter(prop,
                        Query.FilterOperator.EQUAL,
                        propValue);

        return getByQuery(q);
    }

    public List<E> getByQuery(Query query) {
        List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        List<E> result = new ArrayList<E>();
        for (Entity entity: entities) {
            result.add(createDAOEntity(entity));
        }
        return result;
    }

    public int countForQuery(Query query) {
        return datastore.prepare(query).countEntities(FetchOptions.Builder.withDefaults());
    }

    @Override
    public List<E> getByParent(Key parentKey) {
        Query query = new Query(getKind()).setAncestor(parentKey);

        return getByQuery(query);
    }

    @Override
    public List<E> getAll() {
        Query query = new Query(getKind());

        return getByQuery(query);
    }

    @Override
    public List<Key> create(List<E> wrappers) {
        List<Entity> entities = new ArrayList<Entity>(wrappers.size());
        for (EntityWrapper wrapper: wrappers) {
            entities.add(wrapper.getEntity());
        }
        return datastore.put(entities);
    }

    @Override
    public Key save(EntityWrapper wrapper) {
        return create(wrapper);
    }

    @Override
    public List<Key> save(List<E> wrappers) {
        return create(wrappers);
    }

    @Override
    public void delete(E entity) {
        datastore.delete(entity.getEntity().getKey());
    }

    @Override
    public Set<Key> deleteAll() {
        Query q = new Query(getKind());

        return deleteForQuery(q);
    }

    @Override
    public Set<Key> deleteForQuery(Query query) {
        query.setKeysOnly();
        List<Entity> entities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());

        Set<Key> keys = new HashSet<Key>(entities.size());

        for (Entity e : entities) {
            keys.add(e.getKey());
        }
        if (!keys.isEmpty()) datastore.delete(keys);
        return keys;
    }
}
