package com.threesome.shopme.models;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by LanAnh on 18/12/2017.
 */

public class Product implements Serializable{
    private String id;
    private String cateId;
    private String storeId;
    private String image;
    private String name;
    private String description;
    private HashMap<String, Integer> mapSize;

    public Product(){}

    public Product(String id, String cateId, String storeId, String image, String name, String description, HashMap<String, Integer> mapSize) {
        this.id = id;
        this.cateId = cateId;
        this.storeId = storeId;
        this.image = image;
        this.name = name;
        this.description = description;
        this.mapSize = mapSize;
    }

    public Product(String name, String description, HashMap<String, Integer> mapSize) {
        this.name = name;
        this.description = description;
        this.mapSize = mapSize;
    }

    public HashMap<String, Integer> getMapSize() {
        return mapSize;
    }

    public void setMapSize(HashMap<String, Integer> mapSize) {
        this.mapSize = mapSize;
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
