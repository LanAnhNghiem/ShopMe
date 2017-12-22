package com.threesome.shopme.LA;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.threesome.shopme.R;
import com.threesome.shopme.models.Product;

public class CreateProductActivity extends AppCompatActivity implements SendProductData {
    private static final String TAG = CreateProductActivity.class.getSimpleName();
    private Product mProduct = new Product();
    private String mCateId = "", mStoreId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        Intent intent = getIntent();
        if(intent.hasExtra("cateId")){
            mCateId = intent.getStringExtra("cateId");
            mStoreId = intent.getStringExtra("storeId");
            Log.d(TAG,mCateId + " "+mStoreId);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CreateProductFragment1 fragmentCreateProduct = new CreateProductFragment1();
        fragmentTransaction.add(R.id.container, fragmentCreateProduct);
        fragmentTransaction.commit();
    }

    @Override
    public void getData(Product product) {
        this.mProduct = product;
        mProduct.setCateId(mCateId);
        mProduct.setStoreId(mStoreId);
        Log.d(TAG, mProduct.getCateId());
        Bundle bundle = new Bundle();
        String productStr = Utils.getGsonParser().toJson(mProduct);
        bundle.putString("product", productStr);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CreateProductFragment2 fragmentCreateProduct = new CreateProductFragment2();
        fragmentCreateProduct.setArguments(bundle);
        fragmentTransaction.replace(R.id.container, fragmentCreateProduct);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
