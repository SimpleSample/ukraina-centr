package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.*;
import com.nagornyi.uc.dao.IUserDAO;
import com.nagornyi.uc.entity.User;

import java.util.List;

/**
 * @author Nagorny
 * Date: 25.04.14
 */
public class UserDAO extends EntityDAO<User> implements IUserDAO {

    @Override
    protected User createDAOEntity(Entity entity) {
        return new User(entity);
    }

    @Override
    protected String getKind() {
        return "User";
    }

    public User getUserByEmail(String email) {
        List<User> users = getByProperty("email", email);
        return users.isEmpty()? null : users.get(0);
    }
}
