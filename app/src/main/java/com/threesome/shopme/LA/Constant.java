package com.threesome.shopme.LA;

import android.content.res.Resources;

/**
 * Created by LanAnh on 15/12/2017.
 */

public class Constant {
    public static final String USER = "Users";
    public static final String CATEGORY = "Categories";
    public static final String NAMEPRODUCT = "name";
    public static final String PRODUCTS_BY_CATEGORY = "ProductsByCategory";
    public static final String CATEGORIES_BY_STORE = "CategoriesByStore";
    public static final String QUANTITY = "quantity";
    public static final String LINKIMAGEPRODUCT = "image";
    public static final int SCREEN_WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
    public static final int CATEGORY_WIDTH = (int)(SCREEN_WIDTH/3-30);
    public static final int PRODUCT_WIDTH = (int) (SCREEN_WIDTH/2);
    public static final double GOLDEN_RATIO = 1.618;
}
