package com.wipro.restaurant.controller;

import com.wipro.restaurant.exception.DoubleBookingException;
import com.wipro.restaurant.exception.TableUnavailableException;
import com.wipro.restaurant.model.Reservation;
import com.wipro.restaurant.model.ReservationRequest;
import com.wipro.restaurant.service.ReservationService;

import java.time.LocalTime;
import java.util.List;

public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    public String postReservations(ReservationRequest request, double baseCost) {
        try {
            Reservation reservation = reservationService.bookTable(request, baseCost);
            return "200 OK -> Reservation confirmed. " + reservation;
        } catch (TableUnavailableException e) {
            return "409 Conflict -> TableUnavailableException: " + e.getMessage();
        } catch (DoubleBookingException e) {
            return "409 Conflict -> DoubleBookingException: " + e.getMessage();
        }
    }

    public String getAvailable(String time, int partySize) {
        LocalTime parsedTime;
        try {
            parsedTime = LocalTime.parse(time);
        } catch (Exception e) {
            return "400 Bad Request -> Invalid time format. Use HH:mm (24-hour).";
        }

        List<Integer> tables = reservationService.getAvailableTables(parsedTime, partySize);
        if (tables.isEmpty()) {
            return "200 OK -> No tables available for party size " + partySize + " at " + parsedTime;
        }
        return "200 OK -> Available tables for party size " + partySize + " at " + parsedTime + ": " + tables;
    }
}
