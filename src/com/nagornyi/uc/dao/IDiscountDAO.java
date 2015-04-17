package com.nagornyi.uc.dao;

import com.nagornyi.uc.entity.Discount;
import com.nagornyi.uc.entity.User;

/**
 * @author Nagornyi
 *         Date: 03.06.14
 */
public interface IDiscountDAO extends DAO<Discount> {

    Discount getDiscountForUser(User user);
}
