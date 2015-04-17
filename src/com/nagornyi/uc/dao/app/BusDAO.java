package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Entity;
import com.nagornyi.uc.dao.IBusDAO;
import com.nagornyi.uc.entity.Bus;

/**
 * @author Nagorny
 *         Date: 12.05.14
 */
public class BusDAO extends EntityDAO<Bus> implements IBusDAO {

    @Override
    protected Bus createDAOEntity(Entity entity) {
        return new Bus(entity);
    }

    @Override
    protected String getKind() {
        return "Bus";
    }
}
