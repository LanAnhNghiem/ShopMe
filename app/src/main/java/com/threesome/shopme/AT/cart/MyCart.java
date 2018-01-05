package com.threesome.shopme.AT.cart;

import java.util.ArrayList;

/**
 * Created by Kunpark_PC on 1/5/2018.
 */

public class MyCart {
    private String idStore;
    private String idCategory;
    private String idProduct;
    private String nameProduct;
    private String linkImageProduct;
    private int sumPrice;
    private ArrayList<DetailCart> arrDetailCart;

    public MyCart() {
    }

    public MyCart(String idStore, String idCategory, String idProduct, String nameProduct, String linkImageProduct, int sumPrice, ArrayList<DetailCart> arrDetailCart) {
        this.idStore = idStore;
        this.idCategory = idCategory;
        this.idProduct = idProduct;
        this.nameProduct = nameProduct;
        this.linkImageProduct = linkImageProduct;
        this.sumPrice = sumPrice;
        this.arrDetailCart = arrDetailCart;
    }

    public String getIdStore() {
        return idStore;
    }

    public void setIdStore(String idStore) {
        this.idStore = idStore;
    }

    public String getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public String getLinkImageProduct() {
        return linkImageProduct;
    }

    public void setLinkImageProduct(String linkImageProduct) {
        this.linkImageProduct = linkImageProduct;
    }

    public int getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(int sumPrice) {
        this.sumPrice = sumPrice;
    }

    public ArrayList<DetailCart> getArrDetailCart() {
        return arrDetailCart;
    }

    public void setArrDetailCart(ArrayList<DetailCart> arrDetailCart) {
        this.arrDetailCart = arrDetailCart;
    }
}
