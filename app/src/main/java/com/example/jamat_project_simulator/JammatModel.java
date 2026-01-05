package com.example.jamat_project_simulator;

public class JammatModel {

    private String id;          // Firestore document ID
    private String name;        // changed from jammatName
    private String description; // optional
    private String startCity;
    private String endCity;
    private String startDate;
    private String endDate;

    // Required empty constructor for Firestore
    public JammatModel() {}

    // Full constructor
    public JammatModel(String name, String description, String startCity, String endCity, String startDate, String endDate) {
        this.name = name;
        this.description = description;
        this.startCity = startCity;
        this.endCity = endCity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // 🔹 Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getStartCity() { return startCity; }
    public String getEndCity() { return endCity; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }

    // 🔹 Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setStartCity(String startCity) { this.startCity = startCity; }
    public void setEndCity(String endCity) { this.endCity = endCity; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}
