package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.nagornyi.uc.dao.IRouteLinkDAO;
import com.nagornyi.uc.entity.RouteLink;

import java.util.List;

/**
 * @author Nagorny
 *         Date: 14.05.14
 */
public class RouteLinkDAO extends EntityDAO<RouteLink> implements IRouteLinkDAO {

    @Override
    protected RouteLink createDAOEntity(Entity entity) {
        return new RouteLink(entity);
    }

    @Override
    protected String getKind() {
        return "RouteLink";
    }

    public List<RouteLink> getRouteLinksByCity (Key cityId) {
        Query.Filter birthYearMinFilter =
                new Query.FilterPredicate("nextCity",
                        Query.FilterOperator.EQUAL,
                        cityId);

        Query.Filter birthYearMaxFilter =
                new Query.FilterPredicate("previousCity",
                        Query.FilterOperator.EQUAL,
                        cityId);

        Query.Filter birthYearRangeFilter =
                Query.CompositeFilterOperator.or(birthYearMinFilter, birthYearMaxFilter);

        return getByFilter(birthYearRangeFilter);
    }
}
