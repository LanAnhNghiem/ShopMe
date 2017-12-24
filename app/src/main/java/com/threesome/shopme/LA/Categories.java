package com.threesome.shopme.LA;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.threesome.shopme.models.Product;

import java.util.List;

/**
 * Created by LanAnh on 24/12/2017.
 */

public class Categories extends ExpandableGroup<Product> {
    public Categories(String title, List<Product> items) {
        super(title, items);
    }

}
