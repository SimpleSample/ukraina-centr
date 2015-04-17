package com.nagornyi.uc.cache;

import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.City;
import com.nagornyi.uc.entity.Country;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Nagornyi
 * Date: 5/27/14
 */
public class CityCache extends EntityCache {

	private static final List<City> cities = Collections.synchronizedList(new ArrayList<City>());

	public static List<City> getAllCities() {
		return cities;
	}

    @Override
    public void fillCache() {
        List<Country> countries = DAOFacade.findAll(Country.class);
        List<City> cities = new ArrayList<City>();
        for (Country country: countries) {
            List<City> forCountry = DAOFacade.findByParent(City.class, country.getKey());
            Collections.sort(forCountry);
            for (City city: forCountry) {
                city.getName();
                city.isHidden();
            }
            cities.addAll(forCountry);
        }
        CityCache.cities.addAll(cities);
    }
}
