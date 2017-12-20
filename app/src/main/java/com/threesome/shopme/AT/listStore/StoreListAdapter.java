package com.threesome.shopme.AT.listStore;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.threesome.shopme.AT.store.Store;
import com.threesome.shopme.R;

import java.util.ArrayList;


/**
 * Created by Kunka on 12/21/2017.
 */

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.StoreListViewHolder> {

    private Context mContext;
    private ArrayList<Store> arrStore;

    public StoreListAdapter(Context mContext, ArrayList<Store> arrStore) {
        this.mContext = mContext;
        this.arrStore = arrStore;
    }

    @Override
    public StoreListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false);
        return new StoreListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoreListViewHolder holder, int position) {
        Store store = arrStore.get(position);
        holder.txtStoreName.setText(store.getNameStore());
        holder.txtStoreAddress.setText(store.getAddressStore());

    }

    @Override
    public int getItemCount() {
        return arrStore.size();
    }

    public class StoreListViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgBanerStore;
        public TextView txtStoreName, txtStoreAddress;
        public StoreListViewHolder(View itemView) {
            super(itemView);
            imgBanerStore = itemView.findViewById(R.id.imgBanerStoreList);
            txtStoreName = itemView.findViewById(R.id.txtNameStoreList);
            txtStoreAddress = itemView.findViewById(R.id.txtAddressStoreList);
        }
    }
}
