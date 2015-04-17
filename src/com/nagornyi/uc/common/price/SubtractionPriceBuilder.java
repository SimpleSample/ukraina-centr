package com.nagornyi.uc.common.price;

import com.nagornyi.uc.entity.Price;

/**
 * @author Nagornyi
 * Date: 03.06.14
 */
public class SubtractionPriceBuilder implements PriceBuilder {

    @Override
    public double build(double price, double discountValue) {
        return price - discountValue;
    }
}
