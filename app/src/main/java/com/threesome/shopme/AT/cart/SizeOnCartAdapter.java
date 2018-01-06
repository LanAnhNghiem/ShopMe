package com.threesome.shopme.AT.cart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.threesome.shopme.R;

import java.util.ArrayList;

/**
 * Created by Kunpark_PC on 1/5/2018.
 */

public class SizeOnCartAdapter extends RecyclerView.Adapter<SizeOnCartViewHolder> {

    private ArrayList<DetailCart> arrDetailCart;
    private Context mContext;
    private boolean flagEdit = false;
    private int countProduct = 0;

    public SizeOnCartAdapter(ArrayList<DetailCart> arrDetailCart, Context mContext) {
        this.arrDetailCart = arrDetailCart;
        this.mContext = mContext;
    }

    @Override
    public SizeOnCartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_size_cart, parent, false);
        return new SizeOnCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SizeOnCartViewHolder holder, int position) {
        DetailCart detailCart = arrDetailCart.get(position);
        if (detailCart != null){
            holder.txtPriceSize.setText(detailCart.getPrice() + " VND");
            holder.txtCountSize.setText(detailCart.getCount() + "");
            holder.txtChangeCountSize.setText(detailCart.getCount() + "");
            holder.txtNameSize.setText(detailCart.getNameSize());

            final int[] count = {arrDetailCart.get(position).getCount()};
            holder.txtEditSizeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!flagEdit){
                        flagEdit = true;
                        holder.layoutInfo.setVisibility(View.GONE);
                        holder.layoutEdit.setVisibility(View.VISIBLE);
                        holder.txtEditSizeItem.setText("Save");
                    }else {
                        flagEdit = false;
                        holder.layoutInfo.setVisibility(View.VISIBLE);
                        holder.layoutEdit.setVisibility(View.GONE);
                        holder.txtEditSizeItem.setText("Edit");
                    }
                }
            });
            holder.imgIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    count[0]++;
                    holder.txtChangeCountSize.setText(countProduct + "");
                }
            });
            holder.imgDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (count[0] == 0) {
                        Toast.makeText(mContext, "You have minximum Product!", Toast.LENGTH_SHORT).show();
                    } else {
                        count[0]--;
                    }
                    holder.txtChangeCountSize.setText(countProduct + "");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrDetailCart.size();
    }

}
