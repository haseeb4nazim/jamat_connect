package com.example.jamat_project_simulator;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class JammatMemberModel {

    private String membershipId;     // Firestore document ID
    private String jammatId;         // Which Jamat user joined
    private String jammatName;       // For display purposes
    private String userId;           // Member's user ID
    private String userEmail;        // Member's email
    private String userName;         // Member's name

    @ServerTimestamp
    private Date joinedDate;         // When user was accepted

    // Required empty constructor for Firestore
    public JammatMemberModel() {}

    // Constructor for creating new member
    public JammatMemberModel(String jammatId, String jammatName, String userId,
                             String userEmail, String userName) {
        this.jammatId = jammatId;
        this.jammatName = jammatName;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
    }

    // Full constructor
    public JammatMemberModel(String membershipId, String jammatId, String jammatName,
                             String userId, String userEmail, String userName, Date joinedDate) {
        this.membershipId = membershipId;
        this.jammatId = jammatId;
        this.jammatName = jammatName;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.joinedDate = joinedDate;
    }

    // Getters
    public String getMembershipId() { return membershipId; }
    public String getJammatId() { return jammatId; }
    public String getJammatName() { return jammatName; }
    public String getUserId() { return userId; }
    public String getUserEmail() { return userEmail; }
    public String getUserName() { return userName; }
    public Date getJoinedDate() { return joinedDate; }

    // Setters
    public void setMembershipId(String membershipId) { this.membershipId = membershipId; }
    public void setJammatId(String jammatId) { this.jammatId = jammatId; }
    public void setJammatName(String jammatName) { this.jammatName = jammatName; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setJoinedDate(Date joinedDate) { this.joinedDate = joinedDate; }
}