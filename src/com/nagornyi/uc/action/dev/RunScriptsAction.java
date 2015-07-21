package com.nagornyi.uc.action.dev;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.action.Action;
import com.nagornyi.uc.action.Authorized;
import com.nagornyi.uc.action.MD5Salt;
import com.nagornyi.uc.common.date.DateFormatter;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.app.UserDAO;
import com.nagornyi.uc.entity.*;
import com.nagornyi.uc.helper.BusHelper;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.*;
import java.util.logging.Logger;

/**
 *
 * new Request('run', {}).send(function(){});
 *
 * @author Nagorny
 * Date: 14.05.14
 */
@Authorized(role = Role.ADMIN)
public class RunScriptsAction implements Action {
    private static String SETRA_HTML = "<div class=\"bus bus-setra\"><table><tbody><tr><td><a href=\"#\">1c</a></td><td><a href=\"#\" class=\"active\">1c</a></td><td><span>1c</span></td><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td></tr><tr><td><a href=\"#\" class=\"blocked\">1c</a></td><td><a href=\"#\">1c</a></td><td><span>1c</span></td><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td></tr><tr><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td><td><span>1c</span></td><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td></tr><tr><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td><td><span>1c</span></td><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td></tr><tr><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td><td><span>1c</span></td><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td></tr><tr><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td><td><span>1c</span></td><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td></tr><tr><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td><td><span>1c</span></td><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td></tr><tr><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td><td><span>1c</span></td><td><span>1c</span></td><td><span>1c</span></td></tr><tr><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td><td><span>1c</span></td><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td></tr><tr><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td><td><span>1c</span></td><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td></tr><tr><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td><td><span>1c</span></td><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td></tr><tr><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td><td><span>1c</span></td><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td></tr><tr><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td><td><span>1c</span></td><td><a href=\"#\">1c</a></td><td><a href=\"#\">1c</a></td></tr></tbody></table></div>";
    private static final Logger log = Logger.getLogger(RunScriptsAction.class.getName());

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        UserDAO dao = DAOFacade.getDAO(User.class);
        User u =  dao.getUserByEmail("forartforces@gmail.com");
//        u.setRole(Role.USER);
        u.setPassword(MD5Salt.encrypt("Fhntv1234", "forartforces@gmail.com"));
        dao.save(u);

        if (true) return;

        // adding one user
        User user = new User();
        user.setName("Admin");
        user.setSurname("Admin");
        user.setUserLocale(DateFormatter.UK_LOCALE);
        user.setRole(Role.ADMIN);
        user.setEmail("info@ukraina-centr.com");
        user.setPassword(MD5Salt.encrypt("uc159753", "info@ukraina-centr.com"));

        DAOFacade.save(user);

        // adding one bus
        Bus setra = new Bus();
        setra.setSeatsNum(50);
        setra.setName("Setra s417HDH");

//        setra.setSchemeHTML(HTMLTemplates.SETRA_HTML);
        DAOFacade.save(setra);
        BusHelper.fillSetra(setra);

        //Filling countries
        Country co1 = getCountry("Україна", "Ukraine", "Ucraina", "Украина", "ukr");
        Country co2 = getCountry("Україна", "Italy", "Italia", "Италия", "it");
        Country co3 = getCountry("Угорщина", "Hungary", "Ungheria", "Венгрия", "hu");
        Country co4 = getCountry("Словенія", "Slovenia", "Slovenia", "Словения", "slo");
        DAOFacade.bulkSave(Arrays.asList(co1, co2, co3, co4));

        //Filling cities 'Кіровоград','Черкаси','Київ','Житомир','Рівне','Львів','Чоп',
        // 'Tornyiszentmiklós','Fernetiči','Mestre','Cesena','Perugia','Foligno','Terni','Roma'
        City c1 = getCity("Кіровоград", "Kirovograd", "Kirovograd", "Кировоград", co1, false);
        City c2 = getCity("Черкаси", "Cherkasy", "Cherkasy", "Черкассы", co1, false);
        City c3 = getCity("Київ", "Kyiv", "Kyiv", "Киев", co1, false);
        City c4 = getCity("Житомир", "Zhytomyr", "Zhytomyr", "Житомир", co1, false);
        City c5 = getCity("Рівне", "Rivne", "Rivne", "Ровно", co1, false);
        City c6 = getCity("Львів", "Lviv", "Lviv", "Львов", co1, false);
        City c7 = getCity("Чоп", "Chop", "Chop", "Чоп", co1, true);

        City c8 = getCity("Торніжентміклош", "Tornyiszentmiklós", "Tornyiszentmiklós", "Торнижентмиклош", co3, true);
        City c9 = getCity("Фернетічі", "Fernetiči", "Fernetiči", "Фернетичи", co4, true);
        City c10 = getCity("Местре", "Mestre", "Mestre", "Местре", co2, false);
        City c11 = getCity("Чезена", "Cesena", "Cesena", "Чезена", co2, false);
        City c12 = getCity("Перуджа", "Perugia", "Perugia", "Перуджа", co2, false);
        City c13 = getCity("Фоліньо", "Foligno", "Foligno", "Фолиньо", co2, false);
        City c14 = getCity("Терні", "Terni", "Terni", "Терни", co2, false);
        City c15 = getCity("Рим", "Roma", "Roma", "Рим", co2, false);

        DAOFacade.bulkSave(Arrays.asList(c1,c2,c3,c4,c5,c6,c7,  c8,c9,c10,c11,c12,c13,c14,c15));

        //Creating Route
		Long fStart = 1400153400000L;
		Long bStart = 1400411700000L;
		Date forthStartDate = new Date(fStart);
		Date backStartDate = new Date(bStart);
        Route route = new Route(Route.Status.ACTIVE, forthStartDate, new Date(1400299200000L),
				backStartDate, new Date(1400565600000L), setra);
        DAOFacade.save(route);
        // Creating Route Links
        RouteLink link1 = new RouteLink(route, c1, c2, null, fStart, 7200000L, bStart, 6300000L);      DAOFacade.save(link1);                          fStart += 7200000L; bStart +=6300000L;
        RouteLink link2 = new RouteLink(route, c2, c3, link1, fStart, 13500000L,bStart, 13500000L);    DAOFacade.save(link2);  link1.setNext(link2);   fStart += 13500000L; bStart +=13500000L;
        RouteLink link3 = new RouteLink(route, c3, c4, link2, fStart, 7200000L, bStart, 7200000L);     DAOFacade.save(link3);  link2.setNext(link3);   fStart += 7200000L; bStart +=7200000L;
        RouteLink link4 = new RouteLink(route, c4, c5, link3, fStart, 9900000L, bStart, 9900000L);     DAOFacade.save(link4);  link3.setNext(link4);   fStart += 9900000L; bStart +=9900000L;
        RouteLink link5 = new RouteLink(route, c5, c6, link4, fStart, 12600000L, bStart, 12600000L);   DAOFacade.save(link5);  link4.setNext(link5);   fStart += 12600000L; bStart +=12600000L;
        RouteLink link6 = new RouteLink(route, c6, c7, link5, fStart, 14400000L, bStart, 15300000L);   DAOFacade.save(link6);  link5.setNext(link6);   fStart += 14400000L; bStart +=15300000L;
        RouteLink link7 = new RouteLink(route, c7, c8, link6, fStart, 28800000L, bStart, 36000000L);   DAOFacade.save(link7);  link6.setNext(link7);   fStart += 28800000L; bStart +=36000000L;
        RouteLink link8 = new RouteLink(route, c8, c9, link7, fStart, 13500000L, bStart, 13500000L);   DAOFacade.save(link8);  link7.setNext(link8);   fStart += 13500000L; bStart +=13500000L;
        RouteLink link9 = new RouteLink(route, c9, c10, link8, fStart, 7200000L, bStart, 6300000L);    DAOFacade.save(link9);  link8.setNext(link9);   fStart += 7200000L; bStart +=6300000L;
        RouteLink link10 = new RouteLink(route, c10, c11, link9, fStart, 9900000L, bStart, 9900000L);  DAOFacade.save(link10); link9.setNext(link10);  fStart += 9900000L; bStart +=9900000L;
        RouteLink link11 = new RouteLink(route, c11, c12, link10, fStart, 9000000L, bStart, 9000000L); DAOFacade.save(link11); link10.setNext(link11); fStart += 9000000L; bStart +=9000000L;
        RouteLink link12 = new RouteLink(route, c12, c13, link11, fStart, 3600000L, bStart, 3600000L); DAOFacade.save(link12); link11.setNext(link12); fStart += 3600000L; bStart +=3600000L;
        RouteLink link13 = new RouteLink(route, c13, c14, link12, fStart, 4500000L, bStart, 4500000L); DAOFacade.save(link13); link12.setNext(link13); fStart += 4500000L; bStart +=4500000L;
        RouteLink link14 = new RouteLink(route, c14, c15, link13, fStart, 4500000L, bStart, 6300000L); DAOFacade.save(link14); link13.setNext(link14);
        //Sum: 145800000L (40 h 30 min), 153900000L (42 h 45 min)
        DAOFacade.bulkSave(Arrays.asList(link1,link2,link3,link4,link5,link6,link7,link8,link9,link10,link11,link12,link13,link14));

        route.setFirstLinkKey(link1.getEntity().getKey());
        route.setLastLinkKey(link14.getEntity().getKey());
        DAOFacade.save(route);

        //Filling prices for each combination if cities
        Price p1 = new Price(route, c6, c10, 100, 180);
        Price p2 = new Price(route, c6, c11, 100, 180);
        Price p3 = new Price(route, c6, c12, 100, 180);
        Price p4 = new Price(route, c6, c13, 110, 200);
        Price p5 = new Price(route, c6, c14, 110, 200);
        Price p6 = new Price(route, c6, c15, 110, 200);

        Price p7 = new Price(route, c5, c10, 100, 180);
        Price p8 = new Price(route, c5, c11, 100, 180);
        Price p9 = new Price(route, c5, c12, 100, 180);
        Price p10 = new Price(route, c5, c13, 110, 200);
        Price p11 = new Price(route, c5, c14, 110, 200);
        Price p12 = new Price(route, c5, c15, 110, 200);

        Price p13 = new Price(route, c4, c10, 100, 180);
        Price p14 = new Price(route, c4, c11, 110, 200);
        Price p15 = new Price(route, c4, c12, 110, 200);
        Price p16 = new Price(route, c4, c13, 120, 220);
        Price p17 = new Price(route, c4, c14, 120, 220);
        Price p18 = new Price(route, c4, c15, 120, 220);

        Price p19 = new Price(route, c3, c10, 100, 180);
        Price p20 = new Price(route, c3, c11, 120, 220);
        Price p21 = new Price(route, c3, c12, 120, 220);
        Price p22 = new Price(route, c3, c13, 120, 220);
        Price p23 = new Price(route, c3, c14, 120, 220);
        Price p24 = new Price(route, c3, c15, 120, 220);

        Price p25 = new Price(route, c2, c10, 110, 210);
        Price p26 = new Price(route, c2, c11, 120, 220);
        Price p27 = new Price(route, c2, c12, 120, 220);
        Price p28 = new Price(route, c2, c13, 120, 220);
        Price p29 = new Price(route, c2, c14, 120, 220);
        Price p30 = new Price(route, c2, c15, 120, 220);

        Price p31 = new Price(route, c1, c10, 120, 220);
        Price p32 = new Price(route, c1, c11, 120, 220);
        Price p33 = new Price(route, c1, c12, 120, 220);
        Price p34 = new Price(route, c1, c13, 120, 220);
        Price p35 = new Price(route, c1, c14, 120, 220);
        Price p36 = new Price(route, c1, c15, 120, 220);

        DAOFacade.bulkSave(Arrays.asList(p1,p2,p3,p4,p5,p6,p7,p8,p9,p10, p11,p12,p13,p14,p15,p16,p17,p18,p19,p20, p21,p22,p23,p24,p25,p26,p27,p28,p29,p30, p31,p32,p33,p34,p35,p36));
    }

    private City getCity(String name, String engName, String itName, String rusName, Country country, boolean isHidden) {
        Map<Locale, String> locales = new HashMap<Locale, String>();
        locales.put(Locale.ENGLISH, engName);
        locales.put(Locale.ITALIAN, itName);
        locales.put(Locale.forLanguageTag("ru"), rusName);
        return new City(name, locales, country, isHidden);
    }

    private Country getCountry(String name, String engName, String itName, String rusName, String id) {
        Map<Locale, String> locales = new HashMap<Locale, String>();
        locales.put(Locale.ENGLISH, engName);
        locales.put(Locale.ITALIAN, itName);
        locales.put(Locale.forLanguageTag("ru"), rusName);
        return new Country(name, locales, id);
    }
}
