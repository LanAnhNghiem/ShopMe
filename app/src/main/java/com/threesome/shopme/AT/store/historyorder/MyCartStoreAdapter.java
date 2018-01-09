package com.threesome.shopme.AT.store.historyorder;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.threesome.shopme.AT.cart.DetailCart;
import com.threesome.shopme.AT.cart.MyCart;
import com.threesome.shopme.AT.cart.SizeOnCartAdapter;
import com.threesome.shopme.LA.GlideApp;
import com.threesome.shopme.R;

import java.util.ArrayList;

/**
 * Created by Nhan on 1/8/2018.
 */

public class MyCartStoreAdapter extends RecyclerView.Adapter<MyCartStoreAdapter.ViewHolder> {

    private ArrayList<MyCart> arrMyCart;
    private Context context;
    private ArrayList<DetailCart> arrDetailCart;
    private SizeOnCartAdapter adapter;
    private boolean flagShow = false;

    public MyCartStoreAdapter(ArrayList<MyCart> arrMyCart, Context context) {
        this.arrMyCart = arrMyCart;
        arrDetailCart = new ArrayList<>();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MyCart myCart = arrMyCart.get(position);
        if (myCart != null){
            holder.txtTotal.setText(myCart.getSumPrice() + " VND");
            holder.txtNameProduct.setText(myCart.getNameProduct());
            StringBuilder mBuilder = new StringBuilder();
            for (int i = 0; i< myCart.getArrDetailCart().size(); i++){
                mBuilder.append(myCart.getArrDetailCart().get(i).getNameSize());
                if (i != myCart.getArrDetailCart().size() - 1) {
                    mBuilder.append(", ");
                }
            }
            holder.txtSize.setText(mBuilder.toString());
            GlideApp.with(context)
                    .load(myCart.getLinkImageProduct())
                    .centerCrop().into(holder.imgProduct);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            holder.recyclerViewSize.setLayoutManager(layoutManager);
            arrDetailCart = myCart.getArrDetailCart();
            adapter = new SizeOnCartAdapter(arrDetailCart, context);
            holder.recyclerViewSize.setAdapter(adapter);
            holder.layoutInfoProductItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!flagShow){
                        flagShow = true;
                        holder.layoutRecyclerViewSize.setVisibility(View.VISIBLE);
                    }else {
                        flagShow = false;
                        holder.layoutRecyclerViewSize.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(arrMyCart == null)
            return 0;
        return arrMyCart.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgProduct;
        public TextView txtNameProduct, txtTotal, txtSize;
        public RecyclerView recyclerViewSize;
        public LinearLayout layoutRecyclerViewSize, layoutInfoProductItem;
        public ViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProductItemCart);
            txtNameProduct = itemView.findViewById(R.id.txtProductNameItemCart);
            txtSize = itemView.findViewById(R.id.txtSizeOrderDetail);
            txtTotal = itemView.findViewById(R.id.txtSumPriceProductDetail);
            recyclerViewSize = itemView.findViewById(R.id.recyclerViewSizeItem);
            layoutRecyclerViewSize = itemView.findViewById(R.id.layoutRecyclerViewSize);
            layoutInfoProductItem= itemView.findViewById(R.id.layoutInfoProductItem);
        }
    }

}
