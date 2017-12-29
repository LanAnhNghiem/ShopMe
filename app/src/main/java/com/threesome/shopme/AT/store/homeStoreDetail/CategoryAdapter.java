package com.threesome.shopme.AT.store.homeStoreDetail;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Category;
import com.threesome.shopme.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Handler;

/**
 * Created by Kunpark_PC on 12/25/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    private ArrayList<Category> arrCtegory;
    public ArrayList<Product> arrProduct;
    public HashMap<String, ArrayList<Product>> mapCategory;
    private ProductAdapter adapter;
    private Context mContext;

    public CategoryAdapter(ArrayList<Category> arrCtegory, Context mContext) {
        this.arrCtegory = arrCtegory;
        this.mContext = mContext;
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
        Category category = arrCtegory.get(position);
        holder.txtCategoryName.setText(category.getName() + " (" + category.getQuantity() + ")");
        holder.recyclerViewCategory.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        arrProduct = mapCategory.get(category.getName());
        if (arrProduct != null) {
            if (arrProduct.size() == 0){
                arrCtegory.remove(position);
            }
            adapter = new ProductAdapter(arrProduct, mContext);
            holder.recyclerViewCategory.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
        }

    }

    @Override
    public int getItemCount() {
        return arrCtegory.size();
    }
}
