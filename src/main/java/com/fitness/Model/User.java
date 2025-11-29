package com.fitness.Model;

public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private int age;
    private float weight;
    private int height;
    private String goal;
    private String dietaryPreference;
    private String fitnessLevel;
    private int availability;
    private String securityQuestion;
    private String securityAnswerHash;
    private byte[] profilePicture;
    private String profilePictureType;

    public User(){}
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
    public int getUserId() { 
        return userId; 
    }

    public void setUserId(int userId) { 
        this.userId = userId; 
    }

    public String getName() { 
        return name; 
    }

    public void setName(String name) { 
        this.name = name; 
    }

    public String getEmail() { 
        return email;
     }

    public void setEmail(String email) { 
        this.email = email;
     }

    public String getPassword() { 
        return password; 
    }

    public void setPassword(String password) { 
        this.password = password; 
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getDietaryPreference() {
        return dietaryPreference;
    }

    public void setDietaryPreference(String dietaryPreference) {
        this.dietaryPreference = dietaryPreference;
    }

    public String getFitnessLevel() {
        return fitnessLevel;
    }

    public void setFitnessLevel(String fitnessLevel) {
        this.fitnessLevel = fitnessLevel;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswerHash() {
        return securityAnswerHash;
    }

    public void setSecurityAnswerHash(String securityAnswerHash) {
        this.securityAnswerHash = securityAnswerHash;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getProfilePictureType() {
        return profilePictureType;
    }

    public void setProfilePictureType(String profilePictureType) {
        this.profilePictureType = profilePictureType;
    }
}

