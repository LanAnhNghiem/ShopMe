package com.threesome.shopme.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.threesome.shopme.CustomMapsActivity;
import com.threesome.shopme.R;

import java.util.ArrayList;

/**
 * Created by Nhan on 11/29/2017.
 */

public class ItemStoreGoogleMap extends RecyclerView.Adapter<ItemStoreGoogleMap.ViewHolder> {

    private static final int NUM = 10;
    private CustomMapsActivity context;
    private int widthItem;
    private ArrayList<String> driverFoundIds;

    public ItemStoreGoogleMap(CustomMapsActivity context, ArrayList<String> driverFoundIds) {
        this.context = context;
        this.driverFoundIds = driverFoundIds;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthScreen = displayMetrics.widthPixels;
        widthItem = (widthScreen - 40);


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_store_google_map, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();

        lp.width = widthItem;
        if (position == 0) {
            lp.setMargins(20, 0, 5, 0);
        } else if (position == NUM - 1) {
            lp.setMargins(5, 0, 20, 0);
        } else {
            lp.setMargins(5, 0, 5, 0);
        }

        holder.itemView.setLayoutParams(lp);

    }

    @Override
    public int getItemCount() {
        if (driverFoundIds == null)
            return 0;
        return driverFoundIds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(context, DetailActivity.class);
//            context.startActivity(intent);

            BottomStoreFragment mBottomStore = BottomStoreFragment.newInstance("Rider bottom sheet");
            mBottomStore.show(context.getSupportFragmentManager(), mBottomStore.getTag());
        }
    }

}
