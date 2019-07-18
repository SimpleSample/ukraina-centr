package com.nagornyi.test.action;

import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.nagornyi.uc.action.dev.RunScriptsAction;
import com.nagornyi.uc.cache.CacheManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * Created by Artem on 06.05.2015.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ UtilsTests.class, RouteTests.class, TripTests.class, TicketTests.class })
public class UCTestSuite {

    protected static final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @BeforeClass
    public static void setUpClass() {
        System.out.println("Master setup");
        helper.setUp();
        //filling db
        try {
            new RunScriptsAction().perform(null, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CacheManager.initCache();

    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println("Master tearDown");
        helper.tearDown();
    }

}