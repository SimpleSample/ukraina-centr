package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Entity;
import com.nagornyi.uc.dao.ICountryDAO;
import com.nagornyi.uc.entity.Country;

/**
 * @author Nagorny
 *  Date: 13.05.14
 */
public class CountryDAO extends EntityDAO<Country> implements ICountryDAO {

    @Override
    protected Country createDAOEntity(Entity entity) {
        return new Country(entity);
    }

    @Override
    protected String getKind() {
        return "Country";
    }
}
