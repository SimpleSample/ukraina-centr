package com.nagornyi.uc.dto;

public class SeatDto {

    private String id;
    private String seatNum;

    public SeatDto(String id, String seatNum) {
        this.id = id;
        this.seatNum = seatNum;
    }

    public String getId() {
        return id;
    }

    public String getSeatNum() {
        return seatNum;
    }
}
