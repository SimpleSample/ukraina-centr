package com.nagornyi.uc.common;

import com.nagornyi.uc.entity.Route;
import com.nagornyi.uc.entity.Trip;

import java.util.List;

/**
 * @author Nagorny
 * Date: 18.05.14
 */
public class RouteSearchResult {

    private Route route;
    private int firstIdx;
    private int lastIdx;

	private Long startMilis;
	private Long endMilis;

    private List<Trip> trips;

    public RouteSearchResult(Route route, int firstIdx, int lastIdx, Long startMilis, Long endMilis) {
        this.route = route;
        this.firstIdx = firstIdx;
        this.lastIdx = lastIdx;
		this.startMilis = startMilis;
		this.endMilis = endMilis;
    }

    public boolean isForth() {
        return firstIdx < lastIdx;
    }

    public Route getRoute() {
        return route;
    }

    public int getFirstIdx() {
        return firstIdx;
    }

    public int getLastIdx() {
        return lastIdx;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

	public Long getStartMilis() {
		return startMilis;
	}

	public Long getEndMilis() {
		return endMilis;
	}
}
