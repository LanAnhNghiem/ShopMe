package com.threesome.shopme.AT.cart;

import java.util.ArrayList;

/**
 * Created by Nhan on 1/7/2018.
 */

public class OrderUser {

    private String idStore;
    private String avatarStore;
    private String nameStore;
    private ArrayList<MyCart> myCartArrayList;

    public OrderUser(String idStore, String avatarStore, String nameStore, ArrayList<MyCart> myCartArrayList) {
        this.idStore = idStore;
        this.avatarStore = avatarStore;
        this.nameStore = nameStore;
        this.myCartArrayList = myCartArrayList;
    }

    public String getIdStore() {
        return idStore;
    }

    public void setIdStore(String idStore) {
        this.idStore = idStore;
    }

    public String getAvatarStore() {
        return avatarStore;
    }

    public void setAvatarStore(String avatarStore) {
        this.avatarStore = avatarStore;
    }

    public String getNameStore() {
        return nameStore;
    }

    public void setNameStore(String nameStore) {
        this.nameStore = nameStore;
    }

    public ArrayList<MyCart> getMyCartArrayList() {
        return myCartArrayList;
    }

    public void setMyCartArrayList(ArrayList<MyCart> myCartArrayList) {
        this.myCartArrayList = myCartArrayList;
    }
}
