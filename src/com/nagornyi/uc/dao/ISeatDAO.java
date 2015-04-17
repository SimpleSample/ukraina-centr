package com.nagornyi.uc.dao;

import com.nagornyi.uc.entity.Bus;
import com.nagornyi.uc.entity.Seat;
import com.nagornyi.uc.entity.Trip;

import java.util.List;

/**
 * @author Nagorny
 *         Date: 13.05.14
 */
public interface ISeatDAO extends DAO<Seat> {

    void fillSeatsForBus(Bus bus);

    List<Seat> getSeats(Bus bus);
}
