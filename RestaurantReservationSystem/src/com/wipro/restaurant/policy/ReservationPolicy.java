package com.wipro.restaurant.policy;

import java.time.LocalTime;

public interface ReservationPolicy {

    int getPriorityScore(LocalTime time);

    double getSurcharge(double baseCost);
}
