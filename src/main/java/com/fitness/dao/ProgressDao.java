package com.fitapp.dao;

import com.fitapp.model.ProgressPoint;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProgressDao {

    // TODO: Replace with actual DB queries later
    public List<ProgressPoint> getProgress(String userId, LocalDate start, LocalDate end) {
        // Generate deterministic dummy data covering [start, end]
        List<ProgressPoint> list = new ArrayList<>();
        LocalDate d = start;
        double weight = 80.0; // starting weight
        int calBase = 2200;

        while (!d.isAfter(end)) {
            int dayIndex = (int) (d.toEpochDay() % 7);
            // Simulate slight weight downward trend
            double dailyWeight = weight - (d.toEpochDay() % 14) * 0.15;
            // Calories vary around base
            int cals = calBase + (dayIndex - 3) * 120;
            // Workouts: 0-2 per day, minutes accordingly
            int wCount = (dayIndex % 3 == 0) ? 2 : (dayIndex % 2);
            int wMins = wCount * 35;

            list.add(new ProgressPoint(d, dailyWeight, cals, wCount, wMins));
            d = d.plusDays(1);
        }
        return list;
    }
}
