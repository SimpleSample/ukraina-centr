package com.nagornyi.uc.dao;

import com.nagornyi.uc.entity.Price;

/**
 * @author Nagorny
 *         Date: 16.05.14
 */
public interface IPriceDAO extends DAO<Price> {

    Price getPriceByCities(String city1Id, String city2Id);
}
