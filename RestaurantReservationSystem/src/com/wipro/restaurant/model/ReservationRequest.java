package com.wipro.restaurant.model;

import java.time.LocalTime;

public class ReservationRequest {

    private String customerName;
    private int partySize;
    private LocalTime reservationTime;

    public ReservationRequest(String customerName, int partySize, LocalTime reservationTime) {
        this.customerName = customerName;
        this.partySize = partySize;
        this.reservationTime = reservationTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getPartySize() {
        return partySize;
    }

    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    public LocalTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    @Override
    public String toString() {
        return "ReservationRequest{customerName='" + customerName + "', partySize=" + partySize
                + ", reservationTime=" + reservationTime + "}";
    }
}
