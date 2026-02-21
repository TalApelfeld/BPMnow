package com.example.bpmnow.models.dj;


import com.google.gson.annotations.SerializedName;


public class DjPlaylist {
    @SerializedName("name")
    private String name;
    @SerializedName("total")
    private int total;

    @SerializedName("url")
    private String url;

    @SerializedName("uri")
    private String uri;

    public DjPlaylist() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
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
