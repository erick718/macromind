package com.fitness.model;

import java.time.LocalDateTime;

public class FoodEntry {
    private int userId;
    private String foodName;
    private int calories;
    private float protein;
    private float carbs;
    private float fat;
    private double consumedOunces;
    private LocalDateTime dateTime;

    public FoodEntry() {}

    // getters & setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public float getProtein() { return protein; }
    public void setProtein(float protein) { this.protein = protein; }

    public float getCarbs() { return carbs; }
    public void setCarbs(float carbs) { this.carbs = carbs; }

    public float getFat() { return fat; }
    public void setFat(float fat) { this.fat = fat; }

    public double getConsumedOunces() { return consumedOunces; }
    public void setConsumedOunces(double consumedOunces) { this.consumedOunces = consumedOunces; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
}