package com.wipro.restaurant.service;

import com.wipro.restaurant.exception.DoubleBookingException;
import com.wipro.restaurant.exception.TableUnavailableException;
import com.wipro.restaurant.model.Reservation;
import com.wipro.restaurant.model.ReservationRequest;
import com.wipro.restaurant.policy.OffPeakPolicy;
import com.wipro.restaurant.policy.PeakHourPolicy;
import com.wipro.restaurant.policy.ReservationPolicy;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReservationService {

    private static final LocalTime PEAK_START = LocalTime.of(19, 0);
    private static final LocalTime PEAK_END = LocalTime.of(21, 0);

    private final Map<Integer, Boolean> tableAvailability = new TreeMap<>();
    private final Map<Integer, Integer> tableCapacity = new HashMap<>();
    private final Map<String, Reservation> activeReservations = new HashMap<>();
    private final List<Reservation> allReservations = new ArrayList<>();

    private int reservationSequence = 1000;

    public ReservationService() {
        initializeTables();
    }

    private void initializeTables() {
        addTableRange(101, 105, 2);
        addTableRange(201, 205, 4);
        addTableRange(301, 305, 6);
        addTableRange(401, 405, 8);
    }

    private void addTableRange(int startId, int endId, int capacity) {
        for (int id = startId; id <= endId; id++) {
            tableAvailability.put(id, true);
            tableCapacity.put(id, capacity);
        }
    }

    private ReservationPolicy selectPolicy(LocalTime time) {
        boolean isPeak = !time.isBefore(PEAK_START) && time.isBefore(PEAK_END);
        return isPeak ? new PeakHourPolicy() : new OffPeakPolicy();
    }

    private String buildBookingKey(String customerName, LocalTime time) {
        return customerName.trim().toLowerCase() + "|" + time;
    }

    public List<Integer> getAvailableTables(LocalTime time, int partySize) {
        List<Integer> available = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> entry : tableAvailability.entrySet()) {
            int tableId = entry.getKey();
            boolean isAvailable = entry.getValue();
            int capacity = tableCapacity.get(tableId);
            if (isAvailable && capacity >= partySize) {
                available.add(tableId);
            }
        }
        Collections.sort(available);
        return available;
    }

    public Reservation bookTable(ReservationRequest request, double baseCost) {
        String key = buildBookingKey(request.getCustomerName(), request.getReservationTime());

        if (activeReservations.containsKey(key)) {
            throw new DoubleBookingException("Customer '" + request.getCustomerName()
                    + "' already has an active reservation at " + request.getReservationTime());
        }

        List<Integer> candidateTables = getAvailableTables(request.getReservationTime(), request.getPartySize());
        if (candidateTables.isEmpty()) {
            throw new TableUnavailableException("No table available for party size "
                    + request.getPartySize() + " at " + request.getReservationTime());
        }

        int tableId = candidateTables.get(0);
        tableAvailability.put(tableId, false);

        ReservationPolicy policy = selectPolicy(request.getReservationTime());
        double surcharge = policy.getSurcharge(baseCost);
        double finalCost = baseCost + surcharge;

        String reservationId = "RES" + (reservationSequence++);
        Reservation reservation = new Reservation(reservationId, request.getCustomerName(), tableId, finalCost);

        activeReservations.put(key, reservation);
        allReservations.add(reservation);

        return reservation;
    }

    public boolean cancelReservation(String customerName, LocalTime time) {
        String key = buildBookingKey(customerName, time);
        Reservation reservation = activeReservations.remove(key);
        if (reservation == null) {
            return false;
        }
        tableAvailability.put(reservation.getTableId(), true);
        return true;
    }

    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(allReservations);
    }

    public Map<Integer, Boolean> getTableAvailabilitySnapshot() {
        return Collections.unmodifiableMap(tableAvailability);
    }
}
