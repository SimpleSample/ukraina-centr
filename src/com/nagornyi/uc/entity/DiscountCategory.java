package com.nagornyi.uc.entity;

import com.nagornyi.uc.entity.Discount;

/**
 * @author Nagornyi
 * Date: 03.06.14
 */
public enum DiscountCategory {
    BABY(new Discount(Discount.Type.MULTIPLY, 50.0, "Дитина до 4-х років")),
    CHILD(new Discount(Discount.Type.MULTIPLY, 30.0, "Дитина до 12-ти років")),
    STUDENT(new Discount(Discount.Type.MULTIPLY, 10.0, "Студент")),
    OLDMAN(new Discount(Discount.Type.MULTIPLY, 10.0, "Пенсіонер")),
    FORSIX(new Discount(Discount.Type.MULTIPLY, 10.0, "Член групи з 6-ти чол.")),
    NONE(new Discount(Discount.Type.SUBTRACT, 0.0, "Без знижок"));

    public final Discount discount;

    DiscountCategory(Discount discount) {
        this.discount = discount;
    }

    public Discount getDiscount() {
        return discount;
    }

}
