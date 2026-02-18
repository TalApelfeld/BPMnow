package com.example.bpmnow.models;

import java.util.List;

public class Club {
    private String name;
    private List<String> genres;
    private String distance;
    private String currentDJ;
    private int imageResId;
    private List<ClubDjEntry> djs;

    // No-arg constructor required for Firestore
    public Club() {}

    public Club(String name, List<String> genres, String distance, String currentDJ) {
        this.name = name;
        this.genres = genres;
        this.distance = distance;
        this.currentDJ = currentDJ;
    }

    public Club(String name, List<String> genres, String distance, String currentDJ, int imageResId) {
        this.name = name;
        this.genres = genres;
        this.distance = distance;
        this.currentDJ = currentDJ;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    public String getDistance() { return distance; }
    public void setDistance(String distance) { this.distance = distance; }

    public String getCurrentDJ() { return currentDJ; }
    public void setCurrentDJ(String currentDJ) { this.currentDJ = currentDJ; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public List<ClubDjEntry> getDjs() { return djs; }
    public void setDjs(List<ClubDjEntry> djs) { this.djs = djs; }
}
