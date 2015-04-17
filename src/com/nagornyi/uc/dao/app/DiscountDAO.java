package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Entity;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IDiscountDAO;
import com.nagornyi.uc.entity.Discount;
import com.nagornyi.uc.entity.User;

import java.util.List;

/**
 * @author Nagornyi
 * Date: 03.06.14
 */
public class DiscountDAO extends EntityDAO<Discount> implements IDiscountDAO {

    @Override
    protected Discount createDAOEntity(Entity entity) {
        return new Discount(entity);
    }

    @Override
    protected String getKind() {
        return Discount.class.getSimpleName();
    }

    public Discount getDiscountForUser(User user) {
        List<Discount> discountList = getByParent(user.getKey());
        if (discountList.isEmpty()) return null;
        return discountList.get(0);
    }
}
