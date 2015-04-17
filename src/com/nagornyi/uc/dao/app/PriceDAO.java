package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.nagornyi.uc.cache.PriceCache;
import com.nagornyi.uc.dao.IPriceDAO;
import com.nagornyi.uc.entity.Bus;
import com.nagornyi.uc.entity.City;
import com.nagornyi.uc.entity.Price;
import com.nagornyi.uc.entity.Route;

import java.util.List;

/**
 * @author Nagorny
 *         Date: 16.05.14
 */
public class PriceDAO extends EntityDAO<Price> implements IPriceDAO {

    @Override
    protected Price createDAOEntity(Entity entity) {
        return new Price(entity);
    }

    @Override
    protected String getKind() {
        return Price.class.getSimpleName();
    }

    public Price getPriceByCities(String city1Id, String city2Id) {
        int id = (city1Id.hashCode() + city2Id.hashCode())*31;
        return PriceCache.getPrice(id);
    }
}
