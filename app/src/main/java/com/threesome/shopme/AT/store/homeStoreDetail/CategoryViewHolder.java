package com.threesome.shopme.AT.store.homeStoreDetail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.threesome.shopme.R;

/**
 * Created by Kunpark_PC on 12/25/2017.
 */

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    public RecyclerView recyclerViewCategory;
    public TextView txtCategoryName;
    public TextView txtMore;
    public CategoryViewHolder(View itemView) {
        super(itemView);
        recyclerViewCategory = itemView.findViewById(R.id.recyclerViewCategoryHome);
        txtCategoryName = itemView.findViewById(R.id.txtCategoryNameItem);
        txtMore = itemView.findViewById(R.id.txtMoreCategory);
    }
}
