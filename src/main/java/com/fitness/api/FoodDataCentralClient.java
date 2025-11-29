package com.fitness.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.fitness.Model.FoodItem;

// NOTE: This class assumes you are using a JSON parsing library (like Gson or Jackson)
// for simplicity, the parsing logic is left as a placeholder for the actual library calls.
public class FoodDataCentralClient {

    // You MUST sign up for a free key at data.gov to replace 'YOUR_API_KEY'
    private static final String API_KEY = "YOUR_API_KEY";
    private static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1/foods/search";
    
    /**
     * Searches the USDA FoodData Central database for a food item.
     * Corresponds to User Story #53.
     * * @param query The food name to search for (e.g., "apple", "chicken breast").
     * @return A list of FoodItem objects found, or an empty list if an error occurs.
     */
    public List<FoodItem> searchFoods(String query) {
        List<FoodItem> results = new ArrayList<>();
        
        try {
            // 1. Construct the API URL with query parameter
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            String fullUrl = String.format("%s?api_key=%s&query=%s&pageSize=25", BASE_URL, API_KEY, encodedQuery);
            URL url = new URL(fullUrl);
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                // Handle API error response (e.g., key invalid, rate limit exceeded)
                System.err.println("API Request Failed. HTTP Error Code: " + conn.getResponseCode());
                return results;
            }

            // 2. Read the JSON Response
            StringBuilder jsonResponse = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    jsonResponse.append(line);
                }
            }
            
            // 3. Parse the JSON and map to FoodItem objects
            // THIS IS WHERE YOU NEED YOUR JSON LIBRARY (Gson/Jackson)
            results = parseJsonResponse(jsonResponse.toString());
            
            conn.disconnect();

        } catch (java.io.IOException e) {
            System.err.println("Network error connecting to FoodData Central API: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Runtime error parsing FoodData Central API response: " + e.getMessage());
        }
        
        return results;
    }

    /**
     * Placeholder method for JSON parsing logic.
     * A robust implementation requires a JSON library (e.g., Gson).
     * @param jsonString The raw JSON string response from the USDA API.
     * @return A list of FoodItem objects.
     */
    @SuppressWarnings("unused")
    private List<FoodItem> parseJsonResponse(String jsonString) {
        List<FoodItem> items = new ArrayList<>();
        
        // --- START JSON PARSING PLACEHOLDER ---
        /* The USDA JSON structure is complex. You need to:
        1. Find the root array/list of foods (e.g., "foods" array).
        2. Iterate through each food item object.
        3. Extract 'description' for mealName.
        4. Loop through the nested 'foodNutrients' array to find specific
           nutrients (e.g., Water: 1003, Energy: 1008, Protein: 1004, etc.).
        5. Calculate the total calories, protein, carbs, and fats 
           (usually based on a 100g serving size provided by the API).
           
        Example using assumed Gson/Jackson structure:
        JsonObject root = JsonParser.parseString(jsonString).getAsJsonObject();
        JsonArray foods = root.getAsJsonArray("foods");
        
        for (JsonElement foodElement : foods) {
            FoodItem item = new FoodItem();
            JsonObject foodObj = foodElement.getAsJsonObject();
            
            // Extract basic name
            item.setMealName(foodObj.get("description").getAsString()); 
            
            // Loop through nutrients to find macros
            // ... (Complex logic to find 1008=Calories, 1004=Protein, etc.)
            
            // For now, we will return a dummy item to prove the concept:
            item.setCalories(350); 
            item.setProtein(25);
            item.setCarbs(30);
            item.setFats(15);
            items.add(item);
        }
        */
        // --- END JSON PARSING PLACEHOLDER ---

        // Returning a dummy item for compilation/demonstration:
        /*  FoodItem dummyItem = new FoodItem();
        dummyItem.setMealName("Placeholder Search Result (Remember to get a real API Key!)");
        dummyItem.setCalories(350);
        dummyItem.setProtein(25);
        dummyItem.setCarbs(30);
        dummyItem.setFats(15);
        items.add(dummyItem);
         */
        
        return items;
    }
}