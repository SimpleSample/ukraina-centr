package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.nagornyi.uc.dao.DAOFacade;

import java.util.Date;

/**
 * @author Nagorny
 * Date: 13.05.14
 */
public class RouteLink extends EntityWrapper {

    private Key previousCity;
    private Key nextCity;
    private RouteLink next;
    private RouteLink previous;

	private Date forthStartDate;
	private Date forthEndDate;
	private Date backStartDate;
	private Date backEndDate;

	public RouteLink(Route route, City previousCity, City nextCity, RouteLink previous, Long forthStartDate, Long forthTimeInterval, Long backStartDate, Long backTimeInterval) {
		super(route.getEntity().getKey());
		if (next != null) setNext(next);
		if (previous != null) setPrevious(previous);
		setPreviousCityKey(previousCity.getEntity().getKey());
		setNextCityKey(nextCity.getEntity().getKey());
		setForthStartDate(new Date(forthStartDate));
		setForthEndDate(new Date(forthStartDate + forthTimeInterval));
		setBackStartDate(new Date(backStartDate));
		setBackEndDate(new Date(backStartDate + backTimeInterval));
	}

    public RouteLink(Entity entity) {
        super(entity);
    }

    public Key getPreviousCityKey() {
		if (previousCity == null) {
			previousCity = getProperty("previousCityKey");
		}
        return previousCity;
    }

    public void setPreviousCityKey(Key previousCityKey) {
        setProperty("previousCityKey", previousCityKey);
    }

    public Key getNextCityKey() {
		if (nextCity == null) {
			nextCity = getProperty("nextCity");
		}
        return nextCity;
    }

    public void setNextCityKey(Key nextCityKey) {
        setProperty("nextCity", nextCityKey);
    }

    public RouteLink getNext() {
        if (next == null) {
            Key key = getProperty("next");
            next = key == null? null : DAOFacade.findByKey(RouteLink.class, key);
        }
        return next;
    }

    public void setNext(RouteLink next) {
        setProperty("next", next.getEntity().getKey());
    }

    public RouteLink getPrevious() {
        if (previous == null) {
            Key  key = (Key)getProperty("prev");
            previous = key == null? null : DAOFacade.findByKey(RouteLink.class, key);
        }
        return previous;
    }

    public void setPrevious(RouteLink previous) {
        setProperty("previous", previous.getEntity().getKey());
    }

//    public Long getForthTimeInterval() {
//        return getProperty("forthTimeInterval");
//    }
//
//    public void setForthTimeInterval(Long forthTimeInterval) {
//        setProperty("forthTimeInterval", forthTimeInterval);
//    }
//
//    public Long getBackTimeInterval() {
//        return getProperty("backTimeInterval");
//    }
//
//    public void setBackTimeInterval(Long backTimeInterval) {
//        setProperty("backTimeInterval", backTimeInterval);
//    }

	public Date getForthStartDate() {
		if (forthStartDate == null) {
			forthStartDate = getProperty("forthStartDate");
		}
		return forthStartDate;
	}

	public void setForthStartDate(Date forthStartDate) {
		setProperty("forthStartDate", forthStartDate);
	}

	public Date getForthEndDate() {
		if (forthEndDate == null) {
			forthEndDate = getProperty("forthEndDate");
		}

		return forthEndDate;
	}

	public void setForthEndDate(Date forthEndDate) {
		setProperty("forthEndDate", forthEndDate);
	}

	public Date getBackStartDate() {
		if (backStartDate == null) {
			backStartDate = getProperty("backStartDate");
		}
		return backStartDate;
	}

	public void setBackStartDate(Date backStartDate) {
		setProperty("backStartDate", backStartDate);
	}

	public Date getBackEndDate() {
		if (backEndDate == null) {
			backEndDate = getProperty("backEndDate");
		}
		return backEndDate;
	}

	public void setBackEndDate(Date backEndDate) {
		setProperty("backEndDate", backEndDate);
	}
}
