package com.example.bpmnow.models.spotify;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SpotifyTrackItem {
    @SerializedName("item")
    private SpotifyPlaylistTrackItem item;

    public SpotifyTrackItem() {
    }

    public SpotifyPlaylistTrackItem getItem() {
        return item;
    }

    public void setItem(SpotifyPlaylistTrackItem item) {
        this.item = item;
    }
}
