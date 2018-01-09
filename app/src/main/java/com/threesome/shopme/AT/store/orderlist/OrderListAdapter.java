package com.threesome.shopme.AT.store.orderlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.threesome.shopme.AT.cart.OrderStore;
import com.threesome.shopme.AT.store.historyorder.OrderDetailActitivy;
import com.threesome.shopme.LA.GlideApp;
import com.threesome.shopme.R;
import com.threesome.shopme.AT.utility.Constant;

import java.util.ArrayList;

/**
 * Created by Nhan on 1/7/2018.
 */

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderListAdapterViewHolder> {


    private Context mContext;
    private ArrayList<OrderStore> orderStores;
    private DatabaseReference mData;

    public OrderListAdapter(ArrayList<OrderStore> orderStores, Context mContext) {
        this.orderStores = orderStores;

        this.mContext = mContext;
        mData = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public OrderListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_orderlist_store, parent, false);
        return new OrderListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderListAdapterViewHolder holder, int position) {
        final OrderStore order = orderStores.get(position);
        if (order != null){
            holder.txtNameUserOrder.setText(order.getCustomer().getName());
            int status = order.getStatus();
            if(status == Constant.CODE_CHUA_NHAN)
                holder.txtStatusOrderStore.setText("Đơn mới");
            else if(status == Constant.CODE_DA_NHAN)
                holder.txtStatusOrderStore.setText("Đang tiến hành");
            else if(status == Constant.CODE_SHIP)
                holder.txtStatusOrderStore.setText("Đang giao");
            else if(status == Constant.CODE_DA_GIAO)
                holder.txtStatusOrderStore.setText("Đã giao");
            else if (status == Constant.CODE_DA_HUY)
                holder.txtStatusOrderStore.setText("Đã hủy");

            if(order.isSeen() == true){
                holder.layoutOrderList.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            else{
                holder.layoutOrderList.setBackgroundColor(Color.parseColor("#777777"));
            }

            GlideApp.with(mContext)
                    .load(order.getCustomer().getAvatarUser())
                    .centerCrop().into(holder.imgAvatarUserOrder);

        }
    }

    @Override
    public int getItemCount() {
        if(orderStores == null)
            return 0;
        return orderStores.size();
    }

    public class OrderListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imgAvatarUserOrder;
        public TextView txtStatusOrderStore, txtNameUserOrder;
        public LinearLayout layoutOrderList;

        public OrderListAdapterViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imgAvatarUserOrder = itemView.findViewById(R.id.imgAvatarUserOrder);
            txtStatusOrderStore = itemView.findViewById(R.id.txtStatusOrderStore);
            txtNameUserOrder = itemView.findViewById(R.id.txtNameUserOrder);
            layoutOrderList = itemView.findViewById(R.id.layoutOrderList);
        }

        @Override
        public void onClick(View view) {
            OrderStore orderStore = orderStores.get(this.getPosition());
            if (orderStore.isSeen() == false){
                //change seen
                mData.child(Constant.ORDERSTORE).child(orderStore.getIdStore()).child(orderStore.getIdOrderOfUser()).child(Constant.SEEN).setValue(true);
            }
            Intent intent = new Intent(mContext, OrderDetailActitivy.class);
            intent.putExtra(Constant.CODE_SEND_ORDER , orderStore);
            mContext.startActivity(intent);
        }
    }

}
