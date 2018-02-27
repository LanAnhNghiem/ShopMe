package com.threesome.shopme.NN.user;

import java.io.Serializable;

/**
 * Created by Nhan on 1/7/2018.
 */

public class Customer implements Serializable {
    private String name;
    private String phonenumber;
    private String avatarUser;

    public Customer(){}

    public Customer(String name, String phonenumber, String avatarUser) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.avatarUser = avatarUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAvatarUser() {
        return avatarUser;
    }

    public void setAvatarUser(String avatarUser) {
        this.avatarUser = avatarUser;
    }
}
