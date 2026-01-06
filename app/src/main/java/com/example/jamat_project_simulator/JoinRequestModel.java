package com.example.jamat_project_simulator;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class JoinRequestModel {

    private String requestId;        // Firestore document ID
    private String jammatId;         // Which Jamat user wants to join
    private String jammatName;       // For display purposes
    private String userId;           // Who sent the request
    private String userEmail;        // User's email
    private String userName;         // User's name
    private String status;           // "pending", "accepted", "rejected"

    @ServerTimestamp
    private Date requestDate;        // When request was sent

    private Date responseDate;       // When admin responded (optional)

    // Required empty constructor for Firestore
    public JoinRequestModel() {}

    // Constructor for creating new request
    public JoinRequestModel(String jammatId, String jammatName, String userId,
                            String userEmail, String userName, String status) {
        this.jammatId = jammatId;
        this.jammatName = jammatName;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.status = status;
    }

    // Full constructor
    public JoinRequestModel(String requestId, String jammatId, String jammatName,
                            String userId, String userEmail, String userName,
                            String status, Date requestDate, Date responseDate) {
        this.requestId = requestId;
        this.jammatId = jammatId;
        this.jammatName = jammatName;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.status = status;
        this.requestDate = requestDate;
        this.responseDate = responseDate;
    }

    // Getters
    public String getRequestId() { return requestId; }
    public String getJammatId() { return jammatId; }
    public String getJammatName() { return jammatName; }
    public String getUserId() { return userId; }
    public String getUserEmail() { return userEmail; }
    public String getUserName() { return userName; }
    public String getStatus() { return status; }
    public Date getRequestDate() { return requestDate; }
    public Date getResponseDate() { return responseDate; }

    // Setters
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public void setJammatId(String jammatId) { this.jammatId = jammatId; }
    public void setJammatName(String jammatName) { this.jammatName = jammatName; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setStatus(String status) { this.status = status; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }
    public void setResponseDate(Date responseDate) { this.responseDate = responseDate; }
}