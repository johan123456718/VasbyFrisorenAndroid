package com.example.vasbyfrisorenandroid.model.setting;

public class Setting {

    private int imgResource;
    private String typeOfSetting;
    private String description;

    public Setting(int imgResource, String typeOfSetting, String description){
        this.imgResource = imgResource;
        this.typeOfSetting = typeOfSetting;
        this.description = description;
    }


    public int getImgResource() {
        return imgResource;
    }

    public String getTypeOfSetting() {
        return typeOfSetting;
    }

    public String getDescription() {
        return description;
    }
}
