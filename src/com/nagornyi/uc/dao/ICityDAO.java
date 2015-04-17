package com.nagornyi.uc.dao;

import com.nagornyi.uc.entity.City;

import java.util.List;

/**
 * @author Nagorny
 *         Date: 13.05.14
 */
public interface ICityDAO extends DAO<City> {

	List<City> getAllCities();
}
