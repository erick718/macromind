package com.fitapp.model;

import java.time.LocalDate;

public class Milestone {
    private LocalDate date;
    private String type;    // e.g., "weight", "workout", "calories"
    private String title;   // e.g., "10 lbs lost"
    private String details; // e.g., "Down from 80kg to 75.5kg"

    public Milestone() {}

    public Milestone(LocalDate date, String type, String title, String details) {
        this.date = date;
        this.type = type;
        this.title = title;
        this.details = details;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
