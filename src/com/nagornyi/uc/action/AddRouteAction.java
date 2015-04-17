package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.Bus;
import com.nagornyi.uc.entity.City;
import com.nagornyi.uc.entity.Route;
import com.nagornyi.uc.entity.RouteLink;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * route :{
 *      busId: "453453453",
 *      links: [
 *              {prev: "prevCityId", next: "nextCityId",  timeInterval:"234209482394"},
 *              {prev: "prevCityId1", next: "nextCityId1", timeInterval:"234209482394"},
 *          ],
 *       startDate:"122131231212312",
 *       endDate:"122131231212312",
 *       status:"active"
 *      }
 *
 * @author Nagorny
 * Date: 13.05.14
 */
@Authorized(role = Role.ADMIN)
public class AddRouteAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String routeStr = req.getParam("route");
        if (routeStr == null) return;

        JSONObject routeObj = new JSONObject(routeStr);
        JSONArray array = routeObj.getJSONArray("links");

        String busId = routeObj.getString("busId");

        Bus bus = DAOFacade.findById(Bus.class, KeyFactory.stringToKey(busId));
        Route route = new Route(bus.getEntity().getKey());
        String startDate = routeObj.getString("startDate");
        String endDate = routeObj.getString("endDate");
        route.setForthStartDate(new Date(Long.parseLong(startDate)));
        route.setForthEndDate(new Date(Long.parseLong(endDate)));

        String status = routeObj.getString("status");
        Route.Status st = status == null? Route.Status.ACTIVE : Route.Status.valueOf(status);
        route.setStatus(st);
        DAOFacade.save(route);

        Key firstLinkId = null;
        Key lastLinkId = null;
        List<RouteLink> linkList = new ArrayList<RouteLink>();

        for (int i = 0, size = array.length(); i < size; i++) {
            JSONObject obj = (JSONObject)array.get(i);
            String prevCityId = obj.getString("prev");
            String nextCityId = obj.getString("next");
            City prevCity = DAOFacade.findById(City.class, KeyFactory.stringToKey(prevCityId));
            City nextCity = DAOFacade.findById(City.class, KeyFactory.stringToKey(nextCityId));

//            String price = obj.getString("price");
            String timeInterval = obj.getString("timeInterval");
            String backTimeInterval = obj.getString("backTimeInterval");
//            RouteLink link = new RouteLink(route, prevCity, nextCity, Long.parseLong(timeInterval), Long.parseLong(backTimeInterval));
//            DAOFacade.save(link);
//            linkList.add(link);
        }

        for (int i = 0, size = linkList.size(); i < size; i++) {
            RouteLink l = linkList.get(i);
            if (i < size-1) l.setNext(linkList.get(i + 1));
            if (i > 0) l.setPrevious(linkList.get(i - 1));

        }
        //making circle
//        linkList.get(0).setPrevious(linkList.get(linkList.size()-1));
//        linkList.get(linkList.size()-1).setNext(linkList.get(0));

        DAOFacade.bulkSave(linkList);

        route.setFirstLinkKey(firstLinkId);
        route.setLastLinkKey(lastLinkId);
        DAOFacade.save(route);
    }

}
