package com.example.bpmnow.models.clubber;

import com.example.bpmnow.models.dj.Dj;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DjCardItem {
    @SerializedName("uid")
    private String uid;
    @SerializedName("stageName")
    private String stageName;
    @SerializedName("profileImageBase64")
    private String profileImageBase64;
    @SerializedName("genres")
    private List<String> genres;

    public DjCardItem() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getProfileImageBase64() {
        return profileImageBase64;
    }

    public void setProfileImageBase64(String profileImageBase64) {
        this.profileImageBase64 = profileImageBase64;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
