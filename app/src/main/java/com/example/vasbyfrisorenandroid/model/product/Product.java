package com.example.vasbyfrisorenandroid.model.product;

public class Product {

    private int imgResource;
    private String typeOfProduct;
    private int price;

    public Product(int imgResource, String typeOfProduct, int price){
        this.imgResource = imgResource;
        this.typeOfProduct = typeOfProduct;
        this.price = price;
    }

    public int getImgResource() {
        return imgResource;
    }

    public int getPrice() {
        return price;
    }

    public String getTypeOfProduct() {
        return typeOfProduct;
    }
}
