package com.threesome.shopme.AT.store.userStoreDetail;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threesome.shopme.AT.store.homeStoreDetail.CategoryAdapter;
import com.threesome.shopme.AT.store.homeStoreDetail.CategoryNameAdapter;
import com.threesome.shopme.AT.store.homeStoreDetail.HomeStoreDetailFragment;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Category;
import com.threesome.shopme.models.Product;

import java.util.ArrayList;
import java.util.HashMap;

public class UserStoreDetailActivity extends AppCompatActivity {

    private DatabaseReference mCategory, mProduct;private TextView txtAddressStore;

    private HashMap<String, ArrayList<Product>> mapCategory;
    private ArrayList<Category> arrCategory;
    private DatabaseReference mData;
    private CategoryAdapter adapter;
    private RecyclerView recyclerCatgory, recyclerViewCategoryName;
    private CategoryNameAdapter categoryNameAdapter;
    private String idStore;
    private EditText edtSearchProductUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_store_detail);
        addControls ();
        new LoadDataTask().execute();
    }

    private void addControls() {
        recyclerCatgory = findViewById(R.id.recyclerViewCategoryHome1User);
        txtAddressStore = findViewById(R.id.txtAdressBottomStoreUser);
        recyclerViewCategoryName = findViewById(R.id.recyclerCategoryNameUser);
        edtSearchProductUser = findViewById(R.id.edtSearchProductUser);
        //Get Data
        Intent intent = getIntent();
        idStore = intent.getStringExtra(Constant.ID_STORE);
        arrCategory = new ArrayList<>();
        mapCategory = new HashMap<>();
        mData = FirebaseDatabase.getInstance().getReference();
        mCategory = mData.child(com.threesome.shopme.LA.Constant.CATEGORIES_BY_STORE).child(idStore);
        mProduct = mData.child(com.threesome.shopme.LA.Constant.PRODUCTS_BY_CATEGORY);
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
    public void addListCategories(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new CategoryAdapter(arrCategory, this, false, idStore);
        adapter.mapCategory = mapCategory;
        recyclerCatgory.setLayoutManager(layoutManager);
        recyclerCatgory.setAdapter(adapter);

        categoryNameAdapter = new CategoryNameAdapter(arrCategory, this);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategoryName.setLayoutManager(layoutManager1);
        recyclerViewCategoryName.setAdapter(categoryNameAdapter);
        Log.d("MAP", mapCategory + "");

    }
}
