package com.threesome.shopme.models;

/**
 * Created by LanAnh on 18/12/2017.
 */

public class Category {
    private String id;
    private String name;
    private int quantity; //số lượng item trong 1 category
    private String idStore;
    public Category(){
    }
    public Category(String id, String name, int quantity, String idStore) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.idStore = idStore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getIdStore() {
        return idStore;
    }

    public void setIdStore(String idStore) {
        this.idStore = idStore;
    }
}
