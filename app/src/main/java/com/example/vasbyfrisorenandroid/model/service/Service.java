package com.example.vasbyfrisorenandroid.model.service;

import android.os.Parcel;
import android.os.Parcelable;

public class Service implements Parcelable {

    private int imgResource;
    private String serviceTitle;
    private int price;

    public Service(){

    }

    public Service(int imgResource, String text1, int price){
        this.imgResource = imgResource;
        this.serviceTitle = text1;
        this.price = price;
    }

    protected Service(Parcel in) {
        imgResource = in.readInt();
        serviceTitle = in.readString();
        price = in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(imgResource);
        parcel.writeString(serviceTitle);
        parcel.writeInt(price);
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };
}
