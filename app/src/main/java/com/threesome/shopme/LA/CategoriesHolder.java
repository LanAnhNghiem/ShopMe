package com.threesome.shopme.LA;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;
import com.threesome.shopme.R;

/**
 * Created by LanAnh on 24/12/2017.
 */

public class CategoriesHolder extends GroupViewHolder {
    public TextView txtTitle;
    public TextView txtMore;
    public CategoriesHolder(View itemView) {
        super(itemView);
        txtTitle = itemView.findViewById(R.id.txtCateTitle);
        txtMore = itemView.findViewById(R.id.txtMore);
    }

    public void setTitle(ExpandableGroup group) {
        this.txtTitle.setText(group.getTitle());
    }
}
