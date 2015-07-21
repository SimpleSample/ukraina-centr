package com.nagornyi.uc.service;

import com.nagornyi.uc.common.DiscountCalculator;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IPriceDAO;
import com.nagornyi.uc.entity.Discount;
import com.nagornyi.uc.entity.DiscountCategory;
import com.nagornyi.uc.entity.Price;
import com.nagornyi.uc.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by artemnagorny on 02.09.15.
 */
public class PriceService implements UCService {

    public double getPrice(String startCityId, String endCityId, User user, DiscountCategory category, boolean isPartial) {
        IPriceDAO priceDAO = DAOFacade.getDAO(Price.class);
        Price p = priceDAO.getPriceByCities(startCityId, endCityId);
        List<Discount> discounts = new ArrayList<Discount>();
        if (user.getDiscount() != null) discounts.add(user.getDiscount());
        if (category != null) discounts.add(category.getDiscount());
        return new DiscountCalculator().calculate(isPartial? p.getPriceBoth()/2 : p.getPrice(), discounts);
    }

}
