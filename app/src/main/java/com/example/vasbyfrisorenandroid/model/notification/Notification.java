package com.example.vasbyfrisorenandroid.model.notification;

public class Notification {

    private int imgResource;
    private String description;
    private String time;

    public Notification(int imgResource, String description, String time){
        this.imgResource = imgResource;
        this.description = description;
        this.time = time;
    }

    public int getImgResource() {
        return imgResource;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }
}
