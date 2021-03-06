package com.nagornyi.uc.dao;

/**
 * @author Nagorny
 *         Date: 12.05.14
 */
public interface DAOFactory {

    <T extends DAO> T getDAO(Class kind);
}
