package com.example.vasbyfrisorenandroid.model.service;

public class Service {

    private int imgResource;
    private String serviceTitle;

    public Service(int imgResource, String text1){
        this.imgResource = imgResource;
        this.serviceTitle = text1;
    }

    public int getImgResource() {
        return imgResource;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }
}
