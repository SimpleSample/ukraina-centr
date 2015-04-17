package com.nagornyi.uc.cache;

import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.Price;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author Nagornyi
 *         Date: 03.06.14
 */
public class PriceCache extends EntityCache {

    private static Map<Integer, Price> prices = new ConcurrentHashMap<Integer, Price>();

    public static Price getPrice(int searchIdx) {
        return prices.get(searchIdx);
    }

    @Override
    public void fillCache() {
        List<Price> all = DAOFacade.findAll(Price.class);
        for (Price price: all) {
            price.getPrice();
            price.getPriceBoth();
            prices.put(price.getSearchIdx(), price);
        }
    }
}
