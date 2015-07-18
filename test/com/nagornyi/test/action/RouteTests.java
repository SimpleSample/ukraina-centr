package com.nagornyi.test.action;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import com.nagornyi.uc.cache.CacheManager;
import com.nagornyi.uc.cache.RouteCache;
import com.nagornyi.uc.common.RouteSearchResult;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ICityDAO;
import com.nagornyi.uc.dao.IRouteDAO;
import com.nagornyi.uc.dao.app.CityDAO;
import com.nagornyi.uc.dao.app.RouteDAO;
import com.nagornyi.uc.entity.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by Artem on 01.05.2015.
 */
public class RouteTests {

    static Long forthStartDate = 1430508600000L;
    static Long fn1 = 1430510400000L;
    static Long fn2 = 1430524200000L;
    static Long forthEndDate = 1430535000000L;
    static Long backStartDate = 1430697900000L;
    static Long bn1 = 1430708700000L;
    static Long bn2 = 1430723100000L;
    static Long backEndDate = 1430724300000L;

    private static Route kyiv_dnipro;

    @Test
    public void checkRoute() {
        String firstCtyStr = "Кременчук";
        String lasCityStr = "Бориспіль";
        ICityDAO cityDao = DAOFacade.getDAO(City.class);
        City kremenchug = cityDao.getByName(firstCtyStr);
        City boryspil = cityDao.getByName(lasCityStr);

        assertNotNull("City \'"+firstCtyStr+"\' wasn't found ", kremenchug);
        assertNotNull("City \'"+lasCityStr+"\' wasn't found ", boryspil);

        RouteSearchResult kyiv_dnipro = RouteCache.getRoute(kremenchug.getKey(), boryspil.getKey());

        assertNotNull("Route \'kyiv_dnipro\' wasn't found ", kyiv_dnipro.getRoute());

        assertEquals(kyiv_dnipro.getRoute().getKey(), this.kyiv_dnipro.getKey());

        assertFalse("Route is forth", kyiv_dnipro.isForth());

        assertEquals(new Long(backEndDate - bn2), new Long(kyiv_dnipro.getStartMilis()));
        assertEquals(new Long(bn1 - backStartDate), new Long(kyiv_dnipro.getEndMilis()));

        City kirovograd = cityDao.getByName("Кіровоград");

        RouteSearchResult unknown_route = RouteCache.getRoute(kremenchug.getKey(), kirovograd.getKey());

        assertNull(unknown_route);
    }

    @BeforeClass
    public static void setUpTests() {
        System.out.println("RouteTests setup");
        Bus bus1 = createTestBus(42, "Ikarus");

        ICityDAO cityDao = DAOFacade.getDAO(City.class);
        City kyiv = cityDao.getByName("Київ");
        Country ukraine = DAOFacade.findByKey(Country.class, kyiv.getParentKey());
        City boryspil = City.valueOf("Бориспіль", "Boryspil", "Boryspil", "Борисполь", ukraine, false);
        City kremenchug = City.valueOf("Кременчук", "Kremenchug", "Kremenchug", "Кременчуг", ukraine, false);
        City dnipro = City.valueOf("Дніпропетровськ", "Dnipropetrovsk", "Dnipropetrovsk", "Днепропетровск", ukraine, false);

        DAOFacade.bulkSave(Arrays.asList(boryspil, kremenchug, dnipro));

        kyiv_dnipro = new Route(Route.Status.ACTIVE, new Date(forthStartDate), new Date(forthEndDate), new Date(backStartDate), new Date(backEndDate), bus1);

        DAOFacade.save(kyiv_dnipro);
        // Creating Route Links
        RouteLink link1 = new RouteLink(kyiv_dnipro, kyiv, boryspil, null, forthStartDate, fn1-forthStartDate, bn2, backEndDate - bn2);
        DAOFacade.save(link1);
        RouteLink link2 = new RouteLink(kyiv_dnipro, boryspil, kremenchug, link1, fn1, fn2 - fn1, bn1, bn2 - bn1);
        DAOFacade.save(link2);
        link1.setNext(link2);
        RouteLink link3 = new RouteLink(kyiv_dnipro, kremenchug, dnipro, link2, fn2, forthEndDate - fn2, backStartDate, bn1-backStartDate);
        DAOFacade.save(link3);
        link2.setNext(link3);
        DAOFacade.bulkSave(Arrays.asList(link1,link2,link3));

        kyiv_dnipro.setFirstLinkKey(link1.getEntity().getKey());
        kyiv_dnipro.setLastLinkKey(link3.getEntity().getKey());
        DAOFacade.save(kyiv_dnipro);

        new RouteCache().reFill();
    }

    public static Bus createTestBus(int seatNum, String name) {
        Bus bus = new Bus();
        bus.setSeatsNum(seatNum);
        bus.setName(name);

        DAOFacade.save(bus);
        return bus;
    }
}
