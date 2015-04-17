package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Entity;
import com.nagornyi.uc.dao.IRouteDAO;
import com.nagornyi.uc.entity.Route;

/**
 * @author Nagorny
 *         Date: 14.05.14
 */
public class RouteDAO extends EntityDAO<Route> implements IRouteDAO {

    @Override
    protected Route createDAOEntity(Entity entity) {
        return new Route(entity);
    }

    @Override
    protected String getKind() {
        return "Route";
    }
}
