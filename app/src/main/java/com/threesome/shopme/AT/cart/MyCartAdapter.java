package com.threesome.shopme.AT.cart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.LA.GlideApp;
import com.threesome.shopme.R;

import java.util.ArrayList;

/**
 * Created by Kunpark_PC on 1/5/2018.
 */

public class MyCartAdapter extends RecyclerView.Adapter<MyCartViewHolder> {

    private ArrayList<MyCart> arrMyCart;
    private ArrayList<DetailCart> arrDetailCart;
    private Context mContext;
    private SizeOnCartAdapter adapter;
    private String idUser;
    private DatabaseReference mData;
    private boolean flagShow = false;

    public MyCartAdapter(ArrayList<MyCart> arrMyCart, Context mContext, String idUser) {
        this.arrMyCart = arrMyCart;
        this.mContext = mContext;
        this.idUser = idUser;
        arrDetailCart = new ArrayList<>();
        mData = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public MyCartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_cart, parent, false);
        return new MyCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyCartViewHolder holder, int position) {
        final MyCart myCart = arrMyCart.get(position);
        if (myCart != null){
            holder.txtTotal.setText(myCart.getSumPrice() + " VND");
            holder.txtNameProduct.setText(myCart.getNameProduct());
            GlideApp.with(mContext)
                    .load(myCart.getLinkImageProduct())
                    .centerCrop().into(holder.imgProduct);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            holder.recyclerViewSize.setLayoutManager(layoutManager);
            arrDetailCart = myCart.getArrDetailCart();
            adapter = new SizeOnCartAdapter(arrDetailCart, mContext);
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
            //delete itemcart
            holder.txtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteItemCart (myCart.getIdStore(), myCart.getIdProduct());
                }
            });
        }
    }

    private void deleteItemCart(final String idStore, final String idProduct) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setMessage("Do you want to delete?");
        mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItemCartOnFireBase (idStore, idProduct);
            }
        });
        mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mBuilder.setCancelable(false);
        mBuilder.show();
    }

    private void deleteItemCartOnFireBase(String idStore, String idProduct) {
        mData.child(Constant.MYCART).child(idUser).child(idStore).child(idProduct).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrMyCart.size();
    }

}
