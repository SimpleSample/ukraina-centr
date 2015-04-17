package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * key = (cityId1.hashCode() + cityId2.hashCode())*31
 *
 * @author Nagorny
 * Date: 15.05.14
 */
public class Price extends EntityWrapper {
    private double price;
    private double priceBoth;
    private int searchIdx;

    public Price(Entity entity) {
        super(entity);
    }

    public Price(Route route, City city1, City city2, double price, double priceBoth) {
        super(route.getEntity().getKey());
        int searchIdx = (KeyFactory.keyToString(city1.getEntity().getKey()).hashCode() +
                KeyFactory.keyToString(city2.getEntity().getKey()).hashCode())*31;
        setSearchIdx(searchIdx);
        setPrice(price);
        setPriceBoth(priceBoth);
    }

    public double getPrice() {
        if (price == 0) {
            price = getProperty("price");
        }
        return price;
    }

    public void setPrice(double price) {
        setProperty("price", price);
    }

    public double getPriceBoth() {
        if (priceBoth == 0) {
            priceBoth = getProperty("priceBoth");
        }
        return priceBoth;
    }

    public void setPriceBoth(double priceBoth) {
        setProperty("priceBoth", priceBoth);
    }

    private void setSearchIdx(int searchIdx) {
        setProperty("searchIdx", searchIdx);
    }

    public int getSearchIdx() {
        return getIntProperty("searchIdx");
    }
}
