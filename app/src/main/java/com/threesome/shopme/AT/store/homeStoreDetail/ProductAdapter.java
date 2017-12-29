package com.threesome.shopme.AT.store.homeStoreDetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.threesome.shopme.LA.Constant;
import com.threesome.shopme.LA.GlideApp;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Category;
import com.threesome.shopme.models.Product;

import java.util.ArrayList;

/**
 * Created by Kunpark_PC on 12/25/2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

    private ArrayList<Product> arrProduct;
    private Context mContext;

    public ProductAdapter(ArrayList<Product> arrProduct, Context mContext) {
        this.arrProduct = arrProduct;
        this.mContext = mContext;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_home, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        try {
            Product product = arrProduct.get(position);
            holder.txtProductName.setText(product.getName());
            holder.txtPrice.setText(product.getPrice() + " VND");
            GlideApp.with(mContext)
                    .load(product.getImage())
                    .centerCrop().into(holder.imgProduct);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrProduct.size();
    }
}
