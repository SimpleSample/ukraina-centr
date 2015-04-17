package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Entity;
import com.nagornyi.uc.cache.CityCache;
import com.nagornyi.uc.dao.ICityDAO;
import com.nagornyi.uc.entity.City;

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
}
