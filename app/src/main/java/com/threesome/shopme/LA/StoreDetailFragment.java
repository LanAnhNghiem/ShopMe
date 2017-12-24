package com.threesome.shopme.LA;

import android.os.AsyncTask;
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
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Category;
import com.threesome.shopme.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by LanAnh on 23/12/2017.
 */

public class StoreDetailFragment extends Fragment {
    private static final String TAG = StoreDetailFragment.class.getSimpleName();
    private CarouselView carouselView;
    private String idStore = "";
    private RecyclerView rvCateSection;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mCategory, mProduct;
    private ArrayList<Category> mCateList = new ArrayList<>();
    private ArrayList<Product> mProList = new ArrayList<>();
    private HashMap<String, ArrayList<Product>> mList = new HashMap<>();
    private int[] samples = {R.drawable.product_default, R.drawable.product_default, R.drawable.product_default, R.drawable.product_default};
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idStore = getArguments().getString(com.threesome.shopme.AT.utility.Constant.ID_STORE);
            Log.d(TAG, idStore);
        }
        mCategory = mData.child(Constant.CATEGORIES_BY_STORE).child(idStore);
        mProduct = mData.child(Constant.PRODUCTS_BY_CATEGORY);
        new LoadDataTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_detail, container, false);
        carouselView = view.findViewById(R.id.carouselView);
        carouselView.setPageCount(samples.length);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(samples[position]);
            }
        });
        rvCateSection = view.findViewById(R.id.rvCateSection);

        return view;
    }
    public void addListCategories(){
        sectionAdapter = new SectionedRecyclerViewAdapter();
        for(Map.Entry<String, ArrayList<Product>> entry: mList.entrySet()){
            CategorySection section = new CategorySection(entry.getKey(), entry.getValue(), getContext());
            sectionAdapter.addSection(section);
            Log.d(TAG, entry.getKey()+ " /////"+entry.getValue());
        }
        GridLayoutManager gridLayout = new GridLayoutManager(getContext(), 2);
        gridLayout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (sectionAdapter.getSectionItemViewType(position)){
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        rvCateSection.setLayoutManager(gridLayout);
        rvCateSection.setAdapter(sectionAdapter);
    }

    public ArrayList<Product> getData(){
        ArrayList<Product> list = new ArrayList<>();
        list.add(new Product("Áo", "150000", "áo thun Thái lan"));
        list.add(new Product("Quần", "150000", "áo thun Thái lan"));
        list.add(new Product("Váy", "150000", "áo thun Thái lan"));
        list.add(new Product("Kẹo", "150000", "áo thun Thái lan"));
        return list;
    }
    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mCategory.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        mList.clear();
                        mProList.clear();
                        mCateList.clear();
                        for(DataSnapshot data: dataSnapshot.getChildren()){
                            Log.d(TAG, String.valueOf(data.child("name").getValue()));
                            final Category category = data.getValue(Category.class);
                            mCateList.add(category);
                            mProduct = mData.child(Constant.PRODUCTS_BY_CATEGORY).child(category.getId());
                            final ArrayList<Product> tmpList = new ArrayList<>();
                            mProduct.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    tmpList.clear();
                                    int index = 0;
                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                        Log.d(TAG, String.valueOf(data.getValue()));
                                        Product product = data.getValue(Product.class);
                                        mProList.add(product);
                                        if(index < 4){
                                            tmpList.add(product);
                                        }
                                        index++;
                                    }
                                    mList.put(category.getName(), tmpList);
                                    publishProgress();
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
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            //hideProgress();
            addListCategories();

        }
    }
//    private void showProgress (){
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//    }
//    private void hideProgress (){
//        progressDialog.hide();
//    }
}
