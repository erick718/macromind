package com.fitness.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fitness.dao.FoodEntryDAO;
import com.fitness.dao.FoodItemDAO;
import com.fitness.model.FoodEntry;
import com.fitness.model.FoodItem;
import com.fitness.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/FoodEntryServlet")
public class FoodEntryServlet extends HttpServlet {
    
    private FoodEntryDAO foodEntryDAO;
    private FoodItemDAO foodItemDAO;

    // USDA API key
    private static final String USDA_API_KEY = "Mbby2oICtTtLuT2uI3Qv2r8ozi6aX4hHd4Sq60KA";

    // Conversion constant
    private static final double OZ_TO_GRAMS = 28.3495;
    
    // Default constructor for container initialization
    public FoodEntryServlet() {
        this(new FoodEntryDAO());
    }
    
    // Constructor for dependency injection (testing)
    public FoodEntryServlet(FoodEntryDAO foodEntryDAO) {
        this.foodEntryDAO = foodEntryDAO;
    }

    @Override
    public void init() {
        if (foodEntryDAO == null) {
            foodEntryDAO = new FoodEntryDAO();
        }
        foodItemDAO = new FoodItemDAO(); // DAO with predefined meals
    }

    /**
     * GET: Two modes:
     * - ?foodName=... : Fetch nutrition info for a single item
     * - ?autocomplete=... : Return a list of suggestion descriptions for dropdown
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String autocompleteQuery = request.getParameter("autocomplete");
        String foodName = request.getParameter("foodName");

        // Autocomplete endpoint
        if (autocompleteQuery != null && !autocompleteQuery.trim().isEmpty()) {
            String encoded = URLEncoder.encode(autocompleteQuery.trim(), StandardCharsets.UTF_8);
            String apiUrl = "https://api.nal.usda.gov/fdc/v1/foods/search?query="
                    + encoded
                    + "&dataType=Survey%20(FNDDS)&pageSize=10&sortBy=score&api_key=" + USDA_API_KEY;

            JSONArray suggestions = new JSONArray();
            try {
                JSONObject payload = callUsda(apiUrl);
                JSONArray foods = payload.optJSONArray("foods");
                if (foods != null) {
                    for (int i = 0; i < foods.length(); i++) {
                        JSONObject f = foods.getJSONObject(i);
                        String desc = f.optString("description");
                        if (desc != null && !desc.isEmpty()) {
                            suggestions.put(desc);
                        }
                    }
                }
            } catch (Exception e) {
                // If API fails, return empty suggestions
            }

            response.setContentType("application/json");
            response.getWriter().write(suggestions.toString());
            return;
        }

        // Single item nutrition lookup
        if (foodName == null || foodName.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing foodName parameter");
            return;
        }

        String encodedName = URLEncoder.encode(foodName.trim(), StandardCharsets.UTF_8);
        String apiUrl = "https://api.nal.usda.gov/fdc/v1/foods/search?query="
                + encodedName
                + "&dataType=Survey%20(FNDDS)&pageSize=1&sortBy=score&api_key=" + USDA_API_KEY;

        JSONObject result = new JSONObject();
        try {
            JSONObject payload = callUsda(apiUrl);
            JSONArray foods = payload.optJSONArray("foods");

            if (foods != null && foods.length() > 0) {
                JSONObject food = foods.getJSONObject(0);
                result.put("name", food.optString("description", foodName));

                JSONArray nutrients = food.optJSONArray("foodNutrients");
                double calories = 0, protein = 0, carbs = 0, fat = 0;

                if (nutrients != null) {
                    for (int i = 0; i < nutrients.length(); i++) {
                        JSONObject n = nutrients.getJSONObject(i);
                        String nutrientName = n.optString("nutrientName");
                        double value = n.optDouble("value", 0);

                        if ("Energy".equalsIgnoreCase(nutrientName)) {
                            calories = value;
                        } else if ("Protein".equalsIgnoreCase(nutrientName)) {
                            protein = value;
                        } else if ("Carbohydrate, by difference".equalsIgnoreCase(nutrientName)) {
                            carbs = value;
                        } else if ("Total lipid (fat)".equalsIgnoreCase(nutrientName)) {
                            fat = value;
                        }
                    }
                }

                // Default serving size assumption: 100g
                result.put("servingSize", 100.0);
                result.put("calories", calories);
                result.put("protein", protein);
                result.put("carbs", carbs);
                result.put("fat", fat);
            }
        } catch (Exception e) {
            result.put("name", foodName);
            result.put("calories", 0);
            result.put("protein", 0);
            result.put("carbs", 0);
            result.put("fat", 0);
            result.put("servingSize", 100.0);
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.write(result.toString());
        out.flush();
    }

    /**
     * POST: Save food entry to DB (supports predefined scaling and manual values)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            String foodName = request.getParameter("foodName");
            double consumedOunces = parseDoubleSafe(request.getParameter("consumed_oz"));
            int calories = parseIntSafe(request.getParameter("calories"));
            double protein = parseDoubleSafe(request.getParameter("protein"));
            double carbs = parseDoubleSafe(request.getParameter("carbs"));
            double fat = parseDoubleSafe(request.getParameter("fat"));
            double servingSize = parseDoubleSafe(request.getParameter("servingSize")); // grams

            // Check if this is a predefined meal
            if (foodItemDAO != null) {
                FoodItem predefined = foodItemDAO.findByName(foodName);
                if (predefined != null && predefined.getServingSize() > 0) {
                    double multiplier = consumedOunces > 0 ? 
                        ((consumedOunces * OZ_TO_GRAMS) / predefined.getServingSize()) : 1.0;
                    calories = (int) Math.round(predefined.getCalories() * multiplier);
                    protein = predefined.getProtein() * multiplier;
                    carbs = predefined.getCarbs() * multiplier;
                    fat = predefined.getFat() * multiplier;
                } else if (servingSize > 0) {
                    // Scale USDA values by weight or default serving size
                    double gramsConsumed = consumedOunces > 0 ? consumedOunces * OZ_TO_GRAMS : servingSize;
                    double multiplier = gramsConsumed / servingSize;
                    calories = (int) Math.round(calories * multiplier);
                    protein = protein * multiplier;
                    carbs = carbs * multiplier;
                    fat = fat * multiplier;
                }
            }

            FoodEntry entry = new FoodEntry();
            entry.setUserId(user.getUserId());
            entry.setFoodName(foodName != null ? foodName : "");
            entry.setCalories(calories);
            entry.setProtein((float) protein);
            entry.setCarbs((float) carbs);
            entry.setFat((float) fat);
            entry.setConsumedOz(consumedOunces);
            entry.setEntryDate(LocalDateTime.now());

            foodEntryDAO.createFoodEntry(entry);
            response.sendRedirect(request.getContextPath() + "/calorieBalance");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/food_entry.jsp?error=true");
        }
    }
    
    private int parseIntSafe(String value) {
        if (value == null || value.trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private double parseDoubleSafe(String value) {
        if (value == null || value.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private JSONObject callUsda(String apiUrl) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return new JSONObject(sb.toString());
        }
    }
}
