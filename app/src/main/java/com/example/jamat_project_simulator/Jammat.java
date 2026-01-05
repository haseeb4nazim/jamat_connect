package com.example.jamat_project_simulator;

public class Jammat {
    private String name, description, startCity, endCity, startDate, endDate;

    public Jammat() {
        // Empty constructor needed for Firestore
    }

    public Jammat(String name, String description, String startCity, String endCity, String startDate, String endDate) {
        this.name = name;
        this.description = description;
        this.startCity = startCity;
        this.endCity = endCity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStartCity() { return startCity; }
    public void setStartCity(String startCity) { this.startCity = startCity; }
    public String getEndCity() { return endCity; }
    public void setEndCity(String endCity) { this.endCity = endCity; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}
