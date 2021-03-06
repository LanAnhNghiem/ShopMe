package com.threesome.shopme.AT.store;

import java.util.HashMap;

/**
 * Created by Kunka on 11/27/2017.
 */

public class Store {
    private String idStore;
    private String nameStore;
    private String linkPhotoStore;
    private String addressStore;
    private String emailStore;
    private String phoneNumber;
    private HashMap<String, Double> mapLocation;

    public Store(String idStore, String nameStore, String linkPhotoStore, String addressStore, String emailStore, String phoneNumber, HashMap<String, Double> mapLocation) {
        this.idStore = idStore;
        this.nameStore = nameStore;
        this.linkPhotoStore = linkPhotoStore;
        this.addressStore = addressStore;
        this.emailStore = emailStore;
        this.phoneNumber = phoneNumber;
        this.mapLocation = mapLocation;
    }

    public HashMap<String, Double> getMapLocation() {
        return mapLocation;
    }

    public void setMapLocation(HashMap<String, Double> mapLocation) {
        this.mapLocation = mapLocation;
    }

    public Store() {
    }

    public String getIdStore() {
        return idStore;
    }

    public void setIdStore(String idStore) {
        this.idStore = idStore;
    }

    public String getNameStore() {
        return nameStore;
    }

    public void setNameStore(String nameStore) {
        this.nameStore = nameStore;
    }

    public String getLinkPhotoStore() {
        return linkPhotoStore;
    }

    public void setLinkPhotoStore(String linkPhotoStore) {
        this.linkPhotoStore = linkPhotoStore;
    }

    public String getAddressStore() {
        return addressStore;
    }

    public void setAddressStore(String addressStore) {
        this.addressStore = addressStore;
    }

    public String getEmailStore() {
        return emailStore;
    }

    public void setEmailStore(String emailStore) {
        this.emailStore = emailStore;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
