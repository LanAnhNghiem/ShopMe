package com.threesome.shopme.AT.cart;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.threesome.shopme.R;

/**
 * Created by Kunpark_PC on 1/5/2018.
 */
public class MyCartViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgProduct;
    public TextView txtNameProduct, txtTotal, txtDelete;
    public RecyclerView recyclerViewSize;
    public LinearLayout layoutRecyclerViewSize, layoutInfoProductItem;
    public MyCartViewHolder(View itemView) {
        super(itemView);
        imgProduct = itemView.findViewById(R.id.imgProductItemCart);
        txtNameProduct = itemView.findViewById(R.id.txtProductNameItemCart);
        txtTotal = itemView.findViewById(R.id.txtSumPriceProductDetail);
        txtDelete = itemView.findViewById(R.id.txtDeleteProductItemCart);
        recyclerViewSize = itemView.findViewById(R.id.recyclerViewSizeItem);
        layoutRecyclerViewSize = itemView.findViewById(R.id.layoutRecyclerViewSize);
        layoutInfoProductItem= itemView.findViewById(R.id.layoutInfoProductItem);
    }
}
