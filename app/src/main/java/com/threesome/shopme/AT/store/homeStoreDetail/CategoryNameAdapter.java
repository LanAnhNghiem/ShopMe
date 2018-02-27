package com.threesome.shopme.AT.store.homeStoreDetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.threesome.shopme.R;
import com.threesome.shopme.models.Category;

import java.util.ArrayList;

/**
 * Created by Kunpark_PC on 12/29/2017.
 */

public class CategoryNameAdapter extends RecyclerView.Adapter<CategoryNameAdapter.CategoryNameViewHolder>{

    private ArrayList<Category> arrCategory;
    private Context mContext;

    public CategoryNameAdapter(ArrayList<Category> arrCategory, Context mContext) {
        this.arrCategory = arrCategory;
        this.mContext = mContext;
    }

    @Override
    public CategoryNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_category_name, parent, false);
        return new CategoryNameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryNameViewHolder holder, int position) {
        Category category = arrCategory.get(position);
        holder.txtCategoryName.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return arrCategory.size();
    }

    public class CategoryNameViewHolder extends RecyclerView.ViewHolder{
        public TextView txtCategoryName;
        public CategoryNameViewHolder(View itemView) {
            super(itemView);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName2);
        }
    }
}
