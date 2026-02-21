package com.example.bpmnow.models.clubber;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrackCardItem {
    @SerializedName("id")
    private String id;
    @SerializedName("uid")
    private String uid;
    @SerializedName("name")
    private String name;
    @SerializedName("artistNames")
    private List<String> artistNames;
    @SerializedName("requestedBy")
    private List<String> requestedBy;
    @SerializedName("url")
    private String url;
    @SerializedName("uri")
    private String uri;

    public TrackCardItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public List<String> getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(List<String> requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
