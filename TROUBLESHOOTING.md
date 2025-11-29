# Troubleshooting Workout Plans Not Displaying

## Issue
Workout plans are not being displayed when clicking on "My Workout Plans" link.

## Debugging Steps Added

### 1. Debug Information in JSP
I've added debug information to the `workout-plans-list.jsp` page that will show:
- User ID
- Whether the plans object is null or not
- Number of plans retrieved
- First plan name (if any plans exist)

### 2. Console Logging in Servlet
Added console output in `WorkoutPlanServlet.java` that will print:
- Number of plans retrieved for the user
- First plan name (if any plans exist)

### 3. Database Test Page
Created a comprehensive test page (`database-test.jsp`) that checks:
- Database connection status
- Whether all required tables exist
- Number of records in each table
- Workout plans for the current user
- Exercise database population status

## How to Debug

### Step 1: Run the Database Test
1. Log into your application
2. Go to the Dashboard
3. Click on "Database Test" button
4. Review all the information displayed

### Step 2: Check Server Console
When you click "My Workout Plans", check your server console (Tomcat logs) for debug messages like:
```
DEBUG: Retrieved X plans for user Y
DEBUG: First plan name: [plan name]
```

### Step 3: Verify Database Setup
The most common cause is that the database tables haven't been created yet. You need to:

1. **Run the SQL Script in MySQL Workbench:**
   - Open MySQL Workbench
   - Connect to your MySQL server
   - Open the `macromind_database_setup.sql` file
   - Execute the entire script
   - This will create all tables and populate 60+ exercises

2. **Verify Database Connection Settings:**
   Make sure `DBConnection.java` has the correct settings:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/macromind";
   private static final String USER = "root"; // Your MySQL username
   private static final String PASSWORD = "3932"; // Your MySQL password
   ```

## Common Issues and Solutions

### Issue 1: Database Connection Failed
**Symptoms:** Database test page shows connection error
**Solutions:**
- Ensure MySQL server is running
- Check username/password in `DBConnection.java`
- Verify database name is "macromind"
- Make sure MySQL JDBC driver is included in project

### Issue 2: Tables Don't Exist
**Symptoms:** Database test shows "Table doesn't exist" errors
**Solutions:**
- Run the complete database setup script in MySQL Workbench
- Make sure you're connected to the right database
- Check if database "macromind" exists

### Issue 3: No Exercises in Database
**Symptoms:** Database test shows 0 exercises
**Solutions:**
- Run the database setup script which includes INSERT statements for exercises
- The workout plan generator needs exercises to create plans

### Issue 4: No Workout Plans Exist
**Symptoms:** Debug info shows 0 plans for user
**Solutions:**
- This is normal for new users
- Generate your first workout plan using the "Generate Workout Plan" feature
- Make sure you have a complete profile (age, weight, activity level, goals)

### Issue 5: Profile Incomplete
**Symptoms:** Error when trying to generate plans
**Solutions:**
- Go to "Update Profile" and fill in all required fields:
  - Age
  - Height
  - Weight  
  - Activity Level
  - Goal
- These are needed for the workout plan algorithm

## Expected Workflow

1. **Setup Database:** Run SQL script in MySQL Workbench
2. **Complete Profile:** Fill in all profile information
3. **Generate Plan:** Use the workout plan generator
4. **View Plans:** Plans should now appear in "My Workout Plans"

## Debug Information Available

The debug version shows:
- **In JSP:** User ID, plans count, debug info box
- **In Console:** Server-side logging of retrieved plans
- **In Test Page:** Complete database diagnostics

Once you identify and fix the issue, you can remove the debug information by:
1. Removing the debug info div from `workout-plans-list.jsp`
2. Removing console.log statements from `WorkoutPlanServlet.java`
3. Removing the "Database Test" link from dashboard (optional)

## Next Steps

1. **First:** Run the database test to identify the specific issue
2. **Then:** Follow the appropriate solution based on the test results
3. **Finally:** Generate a workout plan and verify it appears in the list

Most likely, you just need to run the database setup script in MySQL Workbench to create the tables and populate the exercises!