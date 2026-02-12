package com.example.bpmnow.models;

import java.util.List;

public class Dj {
    private String stageName;
    private List<String> genres;

    public Dj(String stageName, List<String> genres) {
        this.stageName = stageName;
        this.genres = genres;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }
}
