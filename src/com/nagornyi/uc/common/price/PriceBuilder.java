package com.nagornyi.uc.common.price;

import com.nagornyi.uc.entity.Price;

/**
 * @author Nagornyi
 *         Date: 03.06.14
 */
public interface PriceBuilder {

    double build(double price, double discountValue);
}
