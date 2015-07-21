package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.nagornyi.uc.cache.CityCache;
import com.nagornyi.uc.dao.ICityDAO;
import com.nagornyi.uc.entity.City;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author Nagorny
 * Date: 13.05.14
 */
public class CityDAO extends EntityDAO<City> implements ICityDAO {
    @Override
    protected City createDAOEntity(Entity entity) {
        return new City(entity);
    }

    @Override
    protected String getKind() {
        return "City";
    }

    @Override
	public List<City> getAllCities() {
		return CityCache.getAllCities();
	}

    @Override
    public City getByName(String name) {
        Query.Filter whereNameEqualsFilter =
                new Query.FilterPredicate("name",
                        Query.FilterOperator.EQUAL,
                        name);
        List<City> results = getByFilter(whereNameEqualsFilter);
        return CollectionUtils.isEmpty(results)? null : results.iterator().next();
    }
}
