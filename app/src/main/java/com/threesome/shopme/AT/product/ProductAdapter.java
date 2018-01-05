package com.threesome.shopme.AT.product;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.LA.GlideApp;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Product;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kunpark_PC on 12/25/2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

    private ArrayList<Product> arrProduct;
    private Context mContext;
    private boolean isStore;
    private String idStore;

    public ProductAdapter(ArrayList<Product> arrProduct, Context mContext, boolean isStore, String idStore) {
        this.arrProduct = arrProduct;
        this.mContext = mContext;
        this.isStore = isStore;
        this.idStore = idStore;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_product_home, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        try {
            final Product product = arrProduct.get(position);
            holder.txtProductName.setText(product.getName());
            HashMap<String, Integer> mapSize = new HashMap<>();
            mapSize = product.getMapSize();
            int price = 0;
            if (mapSize.containsKey("Big")){
                price = mapSize.get("Big");
            }else if (mapSize.containsKey("Normal")){
                price = mapSize.get("Normal");
            } else if (mapSize.containsKey("Small")){
                price = mapSize.get("Small");
            }
            holder.txtPrice.setText(price + " VND");
            GlideApp.with(mContext)
                    .load(product.getImage())
                    .centerCrop().into(holder.imgProduct);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isStore){
                        Intent intent = new Intent(mContext, DetailProductStoreActivity.class);
                        intent.putExtra(com.threesome.shopme.AT.utility.Constant.ID_PRODUCT, product.getId());
                        intent.putExtra(com.threesome.shopme.AT.utility.Constant.ID_CATEGORY, product.getCateId());
                        intent.putExtra(Constant.ID_STORE, idStore);
                        mContext.startActivity(intent);
                    }else {
                        Intent intent = new Intent(mContext, DetailProductActivity.class);
                        intent.putExtra(com.threesome.shopme.AT.utility.Constant.ID_PRODUCT, product.getId());
                        intent.putExtra(com.threesome.shopme.AT.utility.Constant.ID_CATEGORY, product.getCateId());
                        intent.putExtra(Constant.ID_STORE, idStore);
                        mContext.startActivity(intent);
                    }
                }
            });
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
