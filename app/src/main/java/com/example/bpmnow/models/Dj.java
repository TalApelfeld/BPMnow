package com.example.bpmnow.models;

import java.util.List;

public class Dj {
    private String uid;
    private String stageName;
    private String bio;
    private int age;
    private List<String> genres;
    private String profileImageUrl;
    private String spotifyUserId;
    private boolean spotifyConnected;
    private int totalLikes;
    private int totalRequests;
    private int totalFeedback;

    // No-arg constructor required for Firestore
    public Dj() {}

    // Simple constructor for backward compatibility
    public Dj(String stageName, List<String> genres) {
        this.stageName = stageName;
        this.genres = genres;
    }

    // Full constructor
    public Dj(String djId, String stageName, String bio, int age, List<String> genres,
              String profileImageUrl, String currentClub, String spotifyUserId,
              boolean spotifyConnected, int totalLikes, int totalRequests, int totalFeedback) {
        this.uid = djId;
        this.stageName = stageName;
        this.bio = bio;
        this.age = age;
        this.genres = genres;
        this.profileImageUrl = profileImageUrl;
        this.spotifyUserId = spotifyUserId;
        this.spotifyConnected = spotifyConnected;
        this.totalLikes = totalLikes;
        this.totalRequests = totalRequests;
        this.totalFeedback = totalFeedback;
    }

    public String getDjId() { return uid; }
    public void setDjId(String djId) { this.uid = djId; }

    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getSpotifyUserId() { return spotifyUserId; }
    public void setSpotifyUserId(String spotifyUserId) { this.spotifyUserId = spotifyUserId; }

    public boolean isSpotifyConnected() { return spotifyConnected; }
    public void setSpotifyConnected(boolean spotifyConnected) { this.spotifyConnected = spotifyConnected; }

    public int getTotalLikes() { return totalLikes; }
    public void setTotalLikes(int totalLikes) { this.totalLikes = totalLikes; }

    public int getTotalRequests() { return totalRequests; }
    public void setTotalRequests(int totalRequests) { this.totalRequests = totalRequests; }

    public int getTotalFeedback() { return totalFeedback; }
    public void setTotalFeedback(int totalFeedback) { this.totalFeedback = totalFeedback; }
}
