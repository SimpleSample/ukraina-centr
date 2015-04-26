package com.nagornyi.uc.helper;

import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.Bus;
import com.nagornyi.uc.entity.Seat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Nagorny
 * Date: 13.05.14
 */
public class BusHelper {


    public static void fillSetra(Bus bus) {
        List<Seat> seats = new ArrayList<Seat>(50);
        List<String> letters = new ArrayList<String>(Arrays.asList("a", "b", "c", "d"));
        for (int i = 1; i <= 13; i++) {
            for (String letter : letters) {
                if (i == 8 && (letter.equals("c") || letter.equals("d"))) continue;

                Seat seat = new Seat(bus.getEntity().getKey());
                seat.setSeatNum(i + letter);
                if (i == 1 || i == 2) {
                    seat.setInitiallyBlocked(true);
                } else {
                    seat.setInitiallyBlocked(false);
                }
                seats.add(seat);
            }
        }

        DAOFacade.bulkSave(seats);
    }
}
