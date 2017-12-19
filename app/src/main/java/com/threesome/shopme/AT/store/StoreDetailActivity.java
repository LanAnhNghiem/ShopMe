package com.threesome.shopme.AT.store;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.threesome.shopme.LA.CategoryFragment;
import com.threesome.shopme.R;

public class StoreDetailActivity extends AppCompatActivity {
    LinearLayout btnCreateProduct;
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);
        drawerLayout = findViewById(R.id.drawer_layout);
        btnCreateProduct = findViewById(R.id.btnCreateProduct);
        btnCreateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                CategoryFragment fragmentCategory = new CategoryFragment();
                fragmentTransaction.add(R.id.category_container, fragmentCategory);
                fragmentTransaction.commit();
            }
        });
    }


}
