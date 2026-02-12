package com.example.bpmnow.models;

import java.util.ArrayList;

public class Club {
    private String name;
    private ArrayList<String> genres;
    private String distance;
    private String currentDJ;

    public Club(String name, ArrayList<String> genres, String distance, String currentDJ) {
        this.name = name;
        this.genres = genres;
        this.distance = distance;
        this.currentDJ = currentDJ;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public String getDistance() {
        return distance;
    }

    public String getCurrentDJ() {
        return currentDJ;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenres(ArrayList<String> genres) {
        this.genres = genres;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setCurrentDJ(String currentDJ) {
        this.currentDJ = currentDJ;
    }

}
