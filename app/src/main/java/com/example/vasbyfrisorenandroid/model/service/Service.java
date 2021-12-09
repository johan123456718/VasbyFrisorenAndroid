package com.example.vasbyfrisorenandroid.model.service;

public class Service {

    private int imgResource;
    private String serviceTitle;
    private int price;

    public Service(int imgResource, String text1, int price){
        this.imgResource = imgResource;
        this.serviceTitle = text1;
        this.price = price;
    }

    public int getImgResource() {
        return imgResource;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }


    public int getPrice() {
        return price;
    }
}
