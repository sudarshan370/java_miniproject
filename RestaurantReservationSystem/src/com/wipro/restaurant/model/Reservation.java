package com.wipro.restaurant.model;

public class Reservation {

    private String reservationId;
    private String customerName;
    private int tableId;
    private double finalCost;

    public Reservation(String reservationId, String customerName, int tableId, double finalCost) {
        this.reservationId = reservationId;
        this.customerName = customerName;
        this.tableId = tableId;
        this.finalCost = finalCost;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public double getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(double finalCost) {
        this.finalCost = finalCost;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId
                + " | Customer: " + customerName
                + " | Table: " + tableId
                + " | Final Cost: Rs." + String.format("%.2f", finalCost);
    }
}
