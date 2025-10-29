package com.fitness.model;

import java.util.Date;
import java.time.LocalDateTime;

public class FoodEntry {
    private int entryId;
    private int userId;
    private String foodName;
    private int calories;
    private float protein;
    private float carbs;
    private float fat;
    private double consumed_oz;
    private Date dateTime;

    public FoodEntry() {}

    public FoodEntry(int userId, String foodName, int calories, float protein, float carbs, float fat, String servingSize, Date entryDate) {
        this.userId = userId;
        this.foodName = foodName;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.consumed_oz = consumed_oz;
        this.dateTime = dateTime;
    }

    
    
    public int getEntryId() { return entryId; }
    public void setEntryId(int entryId) { this.entryId = entryId; }

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

    public String getServingSize() { return servingSize; }
    public void setServingSize(String servingSize) { this.servingSize = servingSize; }

    public Date getEntryDate() { return entryDate; }
    public void setEntryDate(Date entryDate) { this.entryDate = entryDate; }
    
}
