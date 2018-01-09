package com.threesome.shopme.AT.store.userStoreDetail;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.threesome.shopme.AT.store.homeStoreDetail.CategoryAdapter;
import com.threesome.shopme.AT.store.homeStoreDetail.CategoryNameAdapter;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Category;
import com.threesome.shopme.models.Product;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by LanAnh on 09/01/2018.
 */

public class UserStoreDetailFragment extends Fragment {
    private DatabaseReference mCategory, mProduct;private TextView txtAddressStore;

    private HashMap<String, ArrayList<Product>> mapCategory;
    private ArrayList<Category> arrCategory;
    private DatabaseReference mData;
    private CategoryAdapter adapter;
    private RecyclerView recyclerCatgory, recyclerViewCategoryName;
    private CategoryNameAdapter categoryNameAdapter;
    private String idStore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get Data
        Intent intent = getActivity().getIntent();
        idStore = intent.getStringExtra(Constant.ID_STORE);
        arrCategory = new ArrayList<>();
        mapCategory = new HashMap<>();
        mData = FirebaseDatabase.getInstance().getReference();
        mCategory = mData.child(com.threesome.shopme.LA.Constant.CATEGORIES_BY_STORE).child(idStore);
        mProduct = mData.child(com.threesome.shopme.LA.Constant.PRODUCTS_BY_CATEGORY);
        new LoadDataTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_store_detail, container, false);
        addControls(view);
        return view;
    }
    private void addControls(View view) {
        recyclerCatgory = view.findViewById(R.id.recyclerViewCategoryHome1User);
        txtAddressStore = view.findViewById(R.id.txtAdressBottomStoreUser);
        recyclerViewCategoryName = view.findViewById(R.id.recyclerCategoryNameUser);
        //edtSearchProductUser = findViewById(R.id.edtSearchProductUser);

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
    public void addListCategories(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new CategoryAdapter(arrCategory, getContext(), false, idStore);
        adapter.mapCategory = mapCategory;
        recyclerCatgory.setLayoutManager(layoutManager);
        recyclerCatgory.setAdapter(adapter);

        categoryNameAdapter = new CategoryNameAdapter(arrCategory, getContext());
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategoryName.setLayoutManager(layoutManager1);
        recyclerViewCategoryName.setAdapter(categoryNameAdapter);
        Log.d("MAP", mapCategory + "");

    }
}
