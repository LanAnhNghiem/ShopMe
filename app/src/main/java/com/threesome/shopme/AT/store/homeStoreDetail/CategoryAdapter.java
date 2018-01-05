package com.threesome.shopme.AT.store.homeStoreDetail;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.threesome.shopme.AT.product.ProductAdapter;
import com.threesome.shopme.LA.ListProductActivity;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Category;
import com.threesome.shopme.models.Product;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kunpark_PC on 12/25/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    private ArrayList<Category> arrCtegory;
    public ArrayList<Product> arrProduct;
    public HashMap<String, ArrayList<Product>> mapCategory;
    private ProductAdapter adapter;
    private Context mContext;
    private boolean isStore;
    private String idStore;

    public CategoryAdapter(ArrayList<Category> arrCtegory, Context mContext, boolean isStore, String idStore) {
        this.arrCtegory = arrCtegory;
        this.mContext = mContext;
        this.isStore = isStore;
        this.idStore = idStore;
        arrProduct = new ArrayList<>();
        mapCategory = new HashMap<>();
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_category_home, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, final int position) {
        final Category category = arrCtegory.get(position);
        arrProduct = mapCategory.get(category.getName());
        if (arrProduct != null) {
            if (arrProduct.size() == 0){
                holder.itemView.setVisibility(View.GONE);
            }else {
                holder.txtCategoryName.setText(category.getName() + " (" + category.getQuantity() + ")");
                holder.recyclerViewCategory.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                adapter = new ProductAdapter(arrProduct, mContext, isStore, idStore);
                holder.recyclerViewCategory.setAdapter(adapter);
                //adapter.notifyDataSetChanged();
                holder.txtMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Category category1 = arrCtegory.get(position);
                        Intent intent = new Intent(mContext, ListProductActivity.class);
                        intent.putExtra("cateId", category1.getId());
                        intent.putExtra("storeId", category1.getIdStore());
                        mContext.startActivity(intent);
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return arrCtegory.size();
    }
}
