package com.nagornyi.uc.action.response;

import com.nagornyi.uc.dto.SeatDto;

import java.util.List;

public class GetAllSeatsResponse {

    private List<SeatDto> allSeats;

    public GetAllSeatsResponse(List<SeatDto> allSeats) {
        this.allSeats = allSeats;
    }

    public List<SeatDto> getAllSeats() {
        return allSeats;
    }
}
