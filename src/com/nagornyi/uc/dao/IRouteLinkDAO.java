package com.nagornyi.uc.dao;

import com.google.appengine.api.datastore.Key;
import com.nagornyi.uc.entity.RouteLink;

import java.util.List;

/**
 * @author Nagorny
 *         Date: 14.05.14
 */
public interface IRouteLinkDAO extends DAO<RouteLink> {

    List<RouteLink> getRouteLinksByCity (Key cityId);
}
