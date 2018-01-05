package com.threesome.shopme.AT.product;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.threesome.shopme.R;

import java.util.ArrayList;

/**
 * Created by Kunpark_PC on 1/3/2018.
 */

public class SizeProductAdapter extends RecyclerView.Adapter<SizeProductViewHolder> {

    private Context mContext;
    private ArrayList<SizeProduct> arrSize;
    private SizeProduct sizeProductSelected;
    private int index = 0, selected;
    private boolean isStore;

    public SizeProductAdapter(Context mContext, ArrayList<SizeProduct> arrSize, boolean isStore) {
        this.mContext = mContext;
        this.arrSize = arrSize;
        this.isStore = isStore;
    }

    @Override
    public SizeProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_size, parent, false);
        return new SizeProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SizeProductViewHolder holder, final int position) {
        SizeProduct sizeProduct = arrSize.get(position);
        if (index == 0) {
            holder.txtSize.setBackgroundResource(R.drawable.bg_size);
            if (arrSize.size() == 1) {
                setSizeProductSelected(arrSize.get(position));
                holder.txtSize.setBackgroundResource(R.drawable.bg_size_selected);
            } else if (arrSize.size() == 2) {
                if (arrSize.get(position).getNameSize().equals("Big")) {
                    setSizeProductSelected(arrSize.get(position));
                    holder.txtSize.setBackgroundResource(R.drawable.bg_size_selected);
                } else if (arrSize.get(position).getNameSize().equals("Normal")) {
                    setSizeProductSelected(arrSize.get(position));
                    holder.txtSize.setBackgroundResource(R.drawable.bg_size_selected);
                }
            } else if (arrSize.size() == 3 && arrSize.get(position).getNameSize().equals("Big")) {
                setSizeProductSelected(arrSize.get(position));
                holder.txtSize.setBackgroundResource(R.drawable.bg_size_selected);
            }

            holder.txtSize.setText(arrSize.get(position).getNameSize().toString());
        }else if (index == 1) {
            if (position == selected){
                holder.txtSize.setBackgroundResource(R.drawable.bg_size_selected);
            }else {
                holder.txtSize.setBackgroundResource(R.drawable.bg_size);
            }
        }
        holder.txtSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = 1;
                selected = position;
                setSizeProductSelected(arrSize.get(selected));
                if (isStore) {
                    DetailProductStoreActivity detailProductStoreActivity = (DetailProductStoreActivity) mContext;
                    detailProductStoreActivity.setPricce(arrSize.get(position).getPriceProduct() + " VND");
                }else {
                    DetailProductActivity detailProductActivity = (DetailProductActivity) mContext;
                    detailProductActivity.setPrice(arrSize.get(position).getPriceProduct() + " VND");
                }
                SizeProductAdapter.this.notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrSize.size();
    }

    public SizeProduct getSizeProductSelected() {
        return sizeProductSelected;
    }

    public void setSizeProductSelected(SizeProduct sizeProductSelected) {
        this.sizeProductSelected = sizeProductSelected;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setArrSize(ArrayList<SizeProduct> arrSize) {
        this.arrSize = arrSize;
    }
}

