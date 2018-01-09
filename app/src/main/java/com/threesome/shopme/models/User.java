package com.threesome.shopme.models;

/**
 * Created by LanAnh on 15/12/2017.
 */

public class User {
    private String id;
    private String email;
    private String userName;
    private String avatar;
    private String phoneNumber;
    private String address;

    public User(){}

    public User(String id, String email, String userName, String avatar) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
