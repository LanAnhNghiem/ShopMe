package com.threesome.shopme.LA;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Product;

/**
 * Created by LanAnh on 24/12/2017.
 */

public class ProductItemHolder extends ChildViewHolder {
    ImageView imgProduct;
    TextView txtName;
    TextView txtPrice;
    public ProductItemHolder(View itemView) {
        super(itemView);
        imgProduct = itemView.findViewById(R.id.imgProduct);
        txtName = itemView.findViewById(R.id.txtName);
        txtPrice = itemView.findViewById(R.id.txtPrice);
    }
    public void onBind(Product product){
        txtPrice.setText(product.getPrice());
        txtName.setText(product.getName());
    }
}
