package com.threesome.shopme.AT.product;

/**
 * Created by Kunpark_PC on 1/3/2018.
 */

public class SizeProduct {
    private String nameSize;
    private String priceProduct;

    public SizeProduct(String nameSize, String priceProduct) {
        this.nameSize = nameSize;
        this.priceProduct = priceProduct;
    }

    public String getNameSize() {
        return nameSize;
    }

    public void setNameSize(String nameSize) {
        this.nameSize = nameSize;
    }

    public String getPriceProduct() {
        return priceProduct;
    }

    public void setPriceProduct(String priceProduct) {
        this.priceProduct = priceProduct;
    }
}
