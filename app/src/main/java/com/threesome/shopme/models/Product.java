package com.threesome.shopme.models;

import java.io.Serializable;

/**
 * Created by LanAnh on 18/12/2017.
 */

public class Product implements Serializable{
    private String id;
    private String cateId;
    private String storeId;
    private String image;
    private String name;
    private String price;
    private String description;

    public Product(){}
    public Product(String name, String price ,String description) {
        this.id = "";
        this.image = "";
        this.name = name;
        this.price = price;
        this.cateId = "";
        this.storeId = "";
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCateId() {
        return cateId;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
