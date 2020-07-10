package com.example.firebase;

public class ProjectData {

    public String title, description, imageUrl, userId;

    public ProjectData(String title, String description, String imageUrl, String userId) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }

    public ProjectData() {
    }

    public ProjectData(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }


}
