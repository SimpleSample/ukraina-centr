package com.nagornyi.test.action;

import static org.junit.Assert.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.nagornyi.uc.action.dev.RunScriptsAction;
import com.nagornyi.uc.cache.CacheManager;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IUserDAO;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Artem on 30.04.2015.
 */
public class AbstractTest {


    // run this test twice to prove we're not leaking any state across tests
    private void doTest() throws JSONException {

        IUserDAO userDao = DAOFacade.getDAO(User.class);
        assertEquals(1, userDao.getAll().size());

        User admin = userDao.getAll().iterator().next();
        assertEquals("info@ukraina-centr.com", admin.getEmail());
        CacheManager.initCache();
//        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
//        assertEquals(0, ds.prepare(new Query("yam")).countEntities(FetchOptions.Builder.withLimit(10)));
//        ds.put(new Entity("yam"));
//        ds.put(new Entity("yam"));
//        assertEquals(2, ds.prepare(new Query("yam")).countEntities(FetchOptions.Builder.withLimit(10)));
    }

}
