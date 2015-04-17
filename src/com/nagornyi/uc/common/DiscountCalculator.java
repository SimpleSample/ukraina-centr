package com.nagornyi.uc.common;

import com.nagornyi.uc.common.price.PercentagePriceBuilder;
import com.nagornyi.uc.common.price.PriceBuilder;
import com.nagornyi.uc.common.price.SubtractionPriceBuilder;
import com.nagornyi.uc.entity.Discount;

import java.util.List;

/**
 * @author Nagornyi
 * Date: 03.06.14
 */
public class DiscountCalculator {

    public double calculate(double price, List<Discount> discounts) {
        double result = price;
        for (Discount discount: discounts) {
            result = calculate(result, discount);
        }
        return result;
    }

    public double calculate(double price, Discount discount) {
        PriceBuilder builder = getBuilderFor(discount.getType());
        return builder.build(price, discount.getValue());
    }

    PriceBuilder getBuilderFor(Discount.Type type) {
        if (type == Discount.Type.SUBTRACT) {
            return new SubtractionPriceBuilder();
        } else if (type == Discount.Type.MULTIPLY) {
            return new PercentagePriceBuilder();
        }
        return null;
    }
}
