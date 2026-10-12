package com.wipro.restaurant.policy;

import java.time.LocalTime;

public class PeakHourPolicy implements ReservationPolicy {

    private static final double SURCHARGE_RATE = 0.15;
    private static final int PRIORITY_SCORE = 10;

    @Override
    public int getPriorityScore(LocalTime time) {
        return PRIORITY_SCORE;
    }

    @Override
    public double getSurcharge(double baseCost) {
        return baseCost * SURCHARGE_RATE;
    }
}
