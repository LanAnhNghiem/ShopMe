package com.threesome.shopme.AT.cart;

import android.support.annotation.NonNull;

import com.threesome.shopme.NN.user.Customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import com.threesome.shopme.AT.utility.Constant;

/**
 * Created by Nhan on 1/7/2018.
 */

public class OrderStore implements Serializable , Comparable<OrderStore>{
    private Customer customer;
    private ArrayList<MyCart> myCarts;
    private int status; // 0 la chua giao 1 la da giao 2 la da huy
    private boolean isSeen;
    private String idOrderOfUser;
    private String idStore;
    private String timeCreate;
    private String idUser;

    public OrderStore(){}

    public OrderStore(Customer customer, ArrayList<MyCart> myCarts ,String idStore , String idOrderOfUser, String timeCreate, String idUser) {
        this.customer = customer;
        this.myCarts = myCarts;
        this.idStore = idStore;
        this.timeCreate = timeCreate;
        status = 0;
        this.idOrderOfUser = idOrderOfUser;
        isSeen = false;
        this.idUser = idUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(String timeCreate) {
        this.timeCreate = timeCreate;
    }

    public String getIdOrderOfUser() {
        return idOrderOfUser;
    }

    public void setIdOrderOfUser(String idOrderOfUser) {
        this.idOrderOfUser = idOrderOfUser;
    }

    public String getIdStore() {
        return idStore;
    }

    public void setIdStore(String idStore) {
        this.idStore = idStore;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ArrayList<MyCart> getMyCarts() {
        return myCarts;
    }

    public void setMyCarts(ArrayList<MyCart> myCarts) {
        this.myCarts = myCarts;
    }

    @Override
    public int compareTo( OrderStore orderStore) {
        return (new Integer(this.getStatus())).compareTo(new Integer(orderStore.getStatus()));
    }
}
