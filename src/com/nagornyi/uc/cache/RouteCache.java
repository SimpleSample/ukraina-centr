package com.nagornyi.uc.cache;

import com.google.appengine.api.datastore.Key;
import com.nagornyi.uc.common.DateFormatter;
import com.nagornyi.uc.common.RouteSearchResult;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.Route;
import com.nagornyi.uc.entity.RouteLink;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author Nagorny
 * Date: 18.05.14
 */
public class RouteCache extends EntityCache {
    private static Logger log = Logger.getLogger(RouteCache.class.getName());

    private static final List<Route> routes = Collections.synchronizedList(new ArrayList<Route>());

    public static RouteSearchResult getRoute(Key prevCityKey, Key nextCityKey) {
		boolean lastCityIsStart = false;
		boolean firstCityIsEnd = false;
		// <RouteIdx, prevCityRouteLinkIdx>
        Map<Integer, Integer> idxes = new HashMap<Integer, Integer>();
        for (int i = 0 , size1 = routes.size(); i < size1; i++) {
            List<RouteLink> links = routes.get(i).getRouteLinks();
            for (int j = 0 , size = links.size(); j < size; j++) {
                RouteLink link = links.get(j);
                if (prevCityKey.equals(link.getPreviousCityKey())) {
                    idxes.put(i, j);
                    break;
                }
                if (j == size - 1 && prevCityKey.equals(link.getNextCityKey())) {
                    idxes.put(i, j);
					firstCityIsEnd = true;
                }
            }
        }
        int listIdx = -1; //idx in the list of route links where both cities were found
        int first = -1; // idx of the start city's RouteLink in RouteLink chain
        int last = -1; // idx of the end city's RouteLink in RouteLink chain
        for (Map.Entry<Integer, Integer> entry: idxes.entrySet()) {
            List<RouteLink> links = routes.get(entry.getKey()).getRouteLinks();
            for (int i = 0 , size1 = links.size(); i < size1; i++) {
                RouteLink link = links.get(i);
                if (nextCityKey.equals(link.getNextCityKey())) {
                    listIdx = entry.getKey();
                    first  = entry.getValue();
                    last = i;
                    break;
                }
                if (i == 0 && nextCityKey.equals(link.getPreviousCityKey())) {
                    listIdx = entry.getKey();
                    first  = entry.getValue();
                    last = i;
					lastCityIsStart = true;
                    break;

                }
            }
        }
		log.info("indexes: first - " + first + ", last - " + last);
		boolean isForth = first < last;
		if (!isForth) { //if the direction is back
			if(!lastCityIsStart) last++;
			if(!firstCityIsEnd) first--;
		}
		log.info("indexes(after correction): first - " + first + ", last - " + last);

		Route targetRoute = routes.get(listIdx);
		List<RouteLink> links = targetRoute.getRouteLinks();
		Date startDate = isForth? links.get(first).getForthStartDate(): links.get(last).getBackStartDate();
		Date endDate = isForth? links.get(last).getForthEndDate(): links.get(first).getBackEndDate();
		log.info("Start date: " + DateFormatter.defaultFormat(startDate) +
				"; end date: " + DateFormatter.defaultFormat(endDate));

		Date routeStartDate = isForth? targetRoute.getForthStartDate() : targetRoute.getBackStartDate();
		Date routeEndDate = isForth? targetRoute.getForthEndDate() : targetRoute.getBackEndDate();
		Long startInterval = startDate.getTime() - routeStartDate.getTime();
		Long endInterval = routeEndDate.getTime() - endDate.getTime();
		Long temp = startInterval;
		startInterval = isForth? startInterval : endInterval;
		endInterval = isForth? endInterval : temp;
        return new RouteSearchResult(routes.get(listIdx), first, last, startInterval, endInterval);
    }

    @Override
    public void fillCache() {
        List<Route> routes = DAOFacade.findAll(Route.class);
        for (Route route: routes) {
            List<RouteLink> result = new ArrayList<RouteLink>();
            RouteLink first = DAOFacade.findByKey(RouteLink.class, route.getFirstLinkKey());
            //caching properties
            first.getForthEndDate();
            first.getForthStartDate();
            first.getBackEndDate();
            first.getBackStartDate();
            first.getNextCityKey();
            first.getPreviousCityKey();

            result.add(first);
            RouteLink next = first.getNext();
            while (next != null) {
                //caching properties
                next.getForthEndDate();
                next.getForthStartDate();
                next.getBackEndDate();
                next.getBackStartDate();
                next.getNextCityKey();
                next.getPreviousCityKey();

                if (result.size() > 30) break; //JIC
                next.setPrevious(result.get(result.size()-1));
                result.add(next);
                next = next.getNext();
            }
            route.setRouteLinks(result);
            RouteCache.routes.add(route);
        }
    }
}
