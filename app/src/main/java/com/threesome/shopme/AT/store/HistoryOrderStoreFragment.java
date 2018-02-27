package com.threesome.shopme.AT.store;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threesome.shopme.AT.cart.OrderStore;
import com.threesome.shopme.AT.cart.OrderStoreComparator;
import com.threesome.shopme.AT.store.orderlist.OrderListAdapter;
import com.threesome.shopme.R;
import com.threesome.shopme.AT.utility.Constant;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryOrderStoreFragment extends Fragment {

    private ArrayList<OrderStore> orderStores;
    private RecyclerView recyclerOrderStore;
    private OrderListAdapter adapter;
    private DatabaseReference mData;
    private String idStore;

    public HistoryOrderStoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            idStore = getArguments().getString(Constant.ID_STORE);
        }
        mData = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_order_store, container, false);
        addControls(view);
        getOrderList();
        return view;
    }

    private void getOrderList() {
        mData.child(Constant.ORDERSTORE).child(idStore).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderStores.clear();
                 if (dataSnapshot.getValue() != null){
                    for (DataSnapshot dt : dataSnapshot.getChildren()){
                        OrderStore order = dt.getValue(OrderStore.class);
                        orderStores.add(order);
                        Collections.sort(orderStores);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addControls(View view) {

        orderStores = new ArrayList<>();
        recyclerOrderStore = view.findViewById(R.id.recyclerOrderList);
        adapter = new OrderListAdapter(orderStores, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerOrderStore.setLayoutManager(layoutManager);
        recyclerOrderStore.setAdapter(adapter);
    }

}
