package com.threesome.shopme.AT.store.homeStoreDetail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.threesome.shopme.R;

/**
 * Created by Kunpark_PC on 12/25/2017.
 */

public class ProductViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgProduct;
    public TextView txtPrice, txtProductName;
    public ProductViewHolder(View itemView) {
        super(itemView);
        imgProduct = itemView.findViewById(R.id.imgProductHome);
        txtPrice = itemView.findViewById(R.id.txtPriceProductHome);
        txtProductName = itemView.findViewById(R.id.txtNameProductHome);
    }
}
