package com.fitness.Model;

public class FoodItem {
    private String name;
    private int calories;
    private double protein;
    private double carbs;
    private double fat;
    private double servingSize; // in ounces

    // Constructor matching your DAO usage
    public FoodItem(String name, int calories, double protein, double carbs, double fat, double servingSize) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.servingSize = servingSize;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    public double getCarbs() { return carbs; }
    public void setCarbs(double carbs) { this.carbs = carbs; }

    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }

    public double getServingSize() { return servingSize; }
    public void setServingSize(double servingSize) { this.servingSize = servingSize; }
}