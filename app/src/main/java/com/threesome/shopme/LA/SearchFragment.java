package com.threesome.shopme.LA;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Category;
import com.threesome.shopme.models.Product;

import java.util.ArrayList;

/**
 * Created by LanAnh on 09/01/2018.
 */

public class SearchFragment extends Fragment {
    private RecyclerView rvProduct;
    private com.threesome.shopme.AT.product.ProductAdapter adapter;
    private GridLayoutManager layoutManager;
    private String searchString = "", idStore= "";
    private ArrayList<Product> mListProduct = new ArrayList<>();
    private DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference productsByCategory ;
    DatabaseReference categoriesByStore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchString = getArguments().getString("search");
            idStore = getArguments().getString("Stores");
        }
        categoriesByStore = mData.child(com.threesome.shopme.LA.Constant.CATEGORIES_BY_STORE).child(idStore);
        getData(searchString);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        rvProduct = view.findViewById(R.id.rvProduct);
        layoutManager = new GridLayoutManager(getContext(), 3);
        rvProduct.setLayoutManager(layoutManager);
        return view;
    }

    private void getData(final String searchString){
        categoriesByStore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //mListCategory.clear();
                mListProduct.clear();
                Log.d("datasnape", dataSnapshot.toString());
                if(!dataSnapshot.exists()){

                }else{
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        Category category = data.getValue(Category.class);
                        //mListCategory.add(category);
                        productsByCategory= mData.child(Constant.PRODUCTS_BY_CATEGORY).child(category.getId());

                        productsByCategory.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Log.d("datasnape", dataSnapshot.toString());
                                if (!dataSnapshot.exists()) {

                                } else {
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        String name = String.valueOf(data.child("name").getValue()).toLowerCase();
                                        if(name.contains(searchString)){
                                            Product product = data.getValue(Product.class);
                                            mListProduct.add(product);
                                            //Log.d(TAG, String.valueOf(data.child("name").getValue()));
                                            //adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                adapter = new com.threesome.shopme.AT.product.ProductAdapter(mListProduct, getContext(), true, idStore);
                                rvProduct.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
