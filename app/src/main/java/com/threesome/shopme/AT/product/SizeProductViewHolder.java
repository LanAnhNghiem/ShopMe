package com.threesome.shopme.AT.product;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.threesome.shopme.LA.ClickListener;
import com.threesome.shopme.R;

/**
 * Created by Kunpark_PC on 1/3/2018.
 */

public class SizeProductViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
    public TextView txtSize;
    public SizeProductViewHolder(View itemView) {
        super(itemView);
        txtSize = itemView.findViewById(R.id.txtSizeProduct);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }
}
