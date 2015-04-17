package com.nagornyi.uc.dao;

import com.nagornyi.uc.entity.User;

/**
 * @author Nagorny
 * Date: 12.05.14
 */
public interface IUserDAO extends DAO<User> {
    User getUserByEmail(String email);
}
