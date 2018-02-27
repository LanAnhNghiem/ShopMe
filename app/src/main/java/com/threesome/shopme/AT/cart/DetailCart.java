package com.threesome.shopme.AT.cart;

import java.io.Serializable;

/**
 * Created by Kunpark_PC on 1/5/2018.
 */

public class DetailCart implements Serializable{
    private String nameSize;
    private String price;
    private String timeOrder;
    private int count;

    public DetailCart() {
    }

    public DetailCart(String nameSize, String price, String timeOrder, int count) {
        this.nameSize = nameSize;
        this.price = price;
        this.timeOrder = timeOrder;
        this.count = count;
    }

    public String getTimeOrder() {
        return timeOrder;
    }

    public void setTimeOrder(String timeOrder) {
        this.timeOrder = timeOrder;
    }

    public String getNameSize() {
        return nameSize;
    }

    public void setNameSize(String nameSize) {
        this.nameSize = nameSize;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
