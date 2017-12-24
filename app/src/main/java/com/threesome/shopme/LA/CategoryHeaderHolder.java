package com.threesome.shopme.LA;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.threesome.shopme.R;

/**
 * Created by LanAnh on 24/12/2017.
 */

public class CategoryHeaderHolder extends RecyclerView.ViewHolder{
    public final TextView txtTitle;
    public final TextView txtMore;
    public CategoryHeaderHolder(View itemView) {
        super(itemView);
        txtTitle = (TextView) itemView.findViewById(R.id.txtCateTitle);
        txtMore = (TextView) itemView.findViewById(R.id.txtMore);
    }
}
