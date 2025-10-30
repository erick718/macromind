package com.fitapp.dao;

import com.fitapp.model.Milestone;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MilestoneDao {

    // TODO: Replace with actual DB queries later
    public List<Milestone> getMilestones(String userId, LocalDate start, LocalDate end) {
        List<Milestone> out = new ArrayList<>();
        // Add a couple of example milestones inside the range if possible
        LocalDate m1 = start.plusDays(5);
        if (!m1.isAfter(end)) {
            out.add(new Milestone(m1, "weight", "5kg milestone", "Reached 75.0kg"));
        }
        LocalDate m2 = start.plusDays(9);
        if (!m2.isAfter(end)) {
            out.add(new Milestone(m2, "workout", "Streak 5 days", "Worked out 5 days in 7"));
        }
        return out;
    }
}
