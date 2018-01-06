package com.threesome.shopme.AT.cart;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.threesome.shopme.R;

/**
 * Created by Kunpark_PC on 1/5/2018.
 */

public class SizeOnCartViewHolder extends RecyclerView.ViewHolder {
    public TextView txtNameSize, txtPriceSize, txtCountSize, txtEditSizeItem, txtChangeCountSize;
    public LinearLayout layoutEdit, layoutInfo;
    public RippleView rippleIncrease, rippleDecrease;
    public ImageView imgDecrease, imgIncrease;
    public SizeOnCartViewHolder(View itemView) {
        super(itemView);
        txtCountSize = itemView.findViewById(R.id.txtCountSizeItemSize);
        txtNameSize = itemView.findViewById(R.id.txtNameSizeItemSize);
        txtPriceSize = itemView.findViewById(R.id.txtPriceItemSize);
        layoutEdit = itemView.findViewById(R.id.layoutEdit);
        layoutInfo = itemView.findViewById(R.id.layoutInfoSize);
        txtEditSizeItem = itemView.findViewById(R.id.txtEditSizeItem);
        txtChangeCountSize= itemView.findViewById(R.id.txtNumberProductOnCart);
        rippleDecrease = itemView.findViewById(R.id.rippleDecreaseOnCart);
        rippleIncrease = itemView.findViewById(R.id.rippleIncreaseOnCart);
        imgDecrease = itemView.findViewById(R.id.imgDecreaseOnCart);
        imgIncrease = itemView.findViewById(R.id.imgIncreaseOnCart);

    }
}
