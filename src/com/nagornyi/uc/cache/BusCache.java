package com.nagornyi.uc.cache;

import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IBusDAO;
import com.nagornyi.uc.entity.Bus;
import com.nagornyi.uc.entity.Seat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author Nagorny
 * Date: 22.05.14
 */
public class BusCache extends EntityCache {
    private static final Map<String, Bus> buses = new ConcurrentHashMap<String, Bus>();

    @Override
    public void fillCache() {
        List<Bus> bs = DAOFacade.findAll(Bus.class);
        for (Bus bus: bs) {
            bus.getSeats();
            buses.put(bus.getStringKey(), bus);
        }
    }

    public static List<Seat> getSeats(Bus bus) {
        return buses.get(bus.getStringKey()).getSeats();
    }

    public static List<Seat> getFreeSeats(String busId, List<Seat> unavailableSeats) {
        Bus bus = buses.get(busId);
        List<Seat> allSeats = bus.getSeats();
        List<Seat> result = new ArrayList<Seat>();

        for (Seat seat: allSeats) {
            if (!unavailableSeats.contains(seat)) result.add(seat);
        }
        return result;
    }

}
