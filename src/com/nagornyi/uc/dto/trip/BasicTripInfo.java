package com.nagornyi.uc.dto.trip;

public class BasicTripInfo {

    private String id;
    private String stringData;
    private int passCount;
    private int allCount;
    private long startDate;
    private boolean routeForth;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStringData() {
        return stringData;
    }

    public void setStringData(String stringData) {
        this.stringData = stringData;
    }

    public int getPassCount() {
        return passCount;
    }

    public void setPassCount(int passCount) {
        this.passCount = passCount;
    }

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public boolean isRouteForth() {
        return routeForth;
    }

    public void setRouteForth(boolean routeForth) {
        this.routeForth = routeForth;
    }
}
