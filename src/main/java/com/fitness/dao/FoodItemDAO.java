package com.fitness.dao;

import java.util.*;
import com.fitness.Model.FoodItem;

public class FoodItemDAO {
    private static final List<FoodItem> defaultFoods = new ArrayList<>();

    static {
        defaultFoods.add(new FoodItem("Chicken Breast", 180, 30, 0, 4, 4)); // 4oz
        defaultFoods.add(new FoodItem("White Rice", 200, 4, 45, 0.4f, 6)); // 6oz
        defaultFoods.add(new FoodItem("Avocado", 240, 3, 12, 22, 5)); // 5oz
        defaultFoods.add(new FoodItem("Egg", 70, 6, 0, 5, 2)); // 2oz
        defaultFoods.add(new FoodItem("Broccoli", 50, 4, 10, 0.5f, 4)); // 4oz
        defaultFoods.add(new FoodItem("Oatmeal", 150, 5, 27, 3, 8)); // 8oz
        defaultFoods.add(new FoodItem("Almonds", 160, 6, 6, 14, 1.5f)); // 1.5oz
        defaultFoods.add(new FoodItem("Greek Yogurt", 100, 10, 5, 0, 6)); // 6oz
        defaultFoods.add(new FoodItem("Tofu", 90, 10, 2, 5, 4)); // 4oz
        defaultFoods.add(new FoodItem("Lentils", 230, 18, 40, 1, 8)); // 8oz
    }

    public List<FoodItem> getAllFoods() {
        return defaultFoods;
    }

    public FoodItem findByName(String name) {
        for (FoodItem item : defaultFoods) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }
}