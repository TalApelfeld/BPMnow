package com.example.bpmnow.models.dj;

import com.example.bpmnow.models.spotify.SpotifyAlbum;
import com.example.bpmnow.models.spotify.SpotifyArtist;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DjTopTrack {
    @SerializedName("name")
    private String name;
    @SerializedName("artistNames")
    private List<String> artistNames;
    @SerializedName("requests")
    private int requests;
    @SerializedName("url")
    private String url;


    public DjTopTrack() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getArtistNames() {
        return artistNames;
    }

    public void setArtistNames(List<String> artistNames) {
        this.artistNames = artistNames;
    }

    public int getRequests() {
        return requests;
    }

    public void setRequests(int requests) {
        this.requests = requests;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
