package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Entity;
import com.nagornyi.uc.cache.BusCache;
import com.nagornyi.uc.dao.ISeatDAO;
import com.nagornyi.uc.entity.Bus;
import com.nagornyi.uc.entity.Seat;
import com.nagornyi.uc.helper.BusHelper;

import java.util.List;

/**
 * @author Nagorny
 * Date: 13.05.14
 */
public class SeatDAO extends EntityDAO<Seat> implements ISeatDAO {

    @Override
    protected Seat createDAOEntity(Entity entity) {
        return new Seat(entity);
    }

    @Override
    protected String getKind() {
        return "Seat";
    }

    @Override
    public void fillSeatsForBus(Bus bus) {
        BusHelper.fillSetra(bus);
    }

    @Override
    public List<Seat> getSeats(Bus bus) {
        return BusCache.getSeats(bus);
    }
}
