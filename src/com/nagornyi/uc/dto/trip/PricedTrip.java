package com.nagornyi.uc.dto.trip;

public class PricedTrip extends BasicTripInfo {

    private double price;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
