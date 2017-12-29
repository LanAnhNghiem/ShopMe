package com.threesome.shopme.AT.store.homeStoreDetail;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Category;
import com.threesome.shopme.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeStoreDetailFragment extends Fragment {


    private CategoryAdapter adapter;
    private TextView txtAddressStore;
    private ArrayList<Category> arrCategory;
    private CategoryNameAdapter categoryNameAdapter;
    private HashMap<String, ArrayList<Product>> mapCategory;
    private ArrayList<Product> arrProduct;
    private RecyclerView recyclerCatgory, recyclerViewCategoryName;
    private String idStore;
    private DatabaseReference mData;
    private DatabaseReference mCategory, mProduct;


    public HomeStoreDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idStore = getArguments().getString(Constant.ID_STORE);
        }
        arrCategory = new ArrayList<>();
        mapCategory = new HashMap<>();
        arrProduct = new ArrayList<>();
        mData = FirebaseDatabase.getInstance().getReference();
        mCategory = mData.child(com.threesome.shopme.LA.Constant.CATEGORIES_BY_STORE).child(idStore);
        mProduct = mData.child(com.threesome.shopme.LA.Constant.PRODUCTS_BY_CATEGORY);
        new LoadDataTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_store_detail, container, false);
        recyclerCatgory = view.findViewById(R.id.recyclerViewCategoryHome1);
        txtAddressStore = view.findViewById(R.id.txtAdressBottomStore);
        recyclerViewCategoryName = view.findViewById(R.id.recyclerCategoryName);
        loadInfoStore ();
        return view;
    }

    private void loadInfoStore() {
        if (idStore != null){
            mData.child(Constant.STORE).child(idStore).child(Constant.STORE_ADDRESS).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        txtAddressStore.setText(dataSnapshot.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void addListCategories(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new CategoryAdapter(arrCategory, getContext());
        adapter.mapCategory = mapCategory;
        recyclerCatgory.setLayoutManager(layoutManager);
        recyclerCatgory.setAdapter(adapter);


        categoryNameAdapter = new CategoryNameAdapter(arrCategory, getContext());
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategoryName.setLayoutManager(layoutManager1);
        recyclerViewCategoryName.setAdapter(categoryNameAdapter);
        Log.d("MAP", mapCategory + "");

    }

    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mCategory.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        mapCategory.clear();
                        arrCategory.clear();
                        for(DataSnapshot data: dataSnapshot.getChildren()){
                            Log.d("TAG", String.valueOf(data.child("name").getValue()));
                            final Category category = data.getValue(Category.class);
                            arrCategory.add(category);
                            mProduct = mData.child(com.threesome.shopme.LA.Constant.PRODUCTS_BY_CATEGORY).child(category.getId());
                            mProduct.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    ArrayList<Product> arrP = new ArrayList<>();
                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                        Log.d("TAG", String.valueOf(data.getValue()));
                                        Product product = data.getValue(Product.class);
                                        if(arrP.size() < 5){
                                            arrP.add(product);
                                        }
                                    }
//
                                    mapCategory.put(category.getName(), arrP);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }

                            });

                        }
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                publishProgress();
                            }
                        }, 500);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            addListCategories();

        }
    }

}
