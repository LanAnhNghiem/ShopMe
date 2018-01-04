package com.threesome.shopme.AT.product;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qhutch.bottomsheetlayout.BottomSheetLayout;
import com.threesome.shopme.AT.store.userStoreDetail.SizeProductAdapter;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.LA.GlideApp;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DetailProductActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardViewProductImage;
    private ImageView imgProduct, imgProductSmall, imgBack, imgLove, imgAddToCart, imgChat;
    private TextView txtBuyNow, txtProductName1, txtProductName2, txtPriceProduct, txtInfoProduct, txtProductName3, txtPriceProductSmall;
    private BottomSheetLayout layout;
    private LinearLayout layoutDetailProduct, layoutLove, layoutComment;
    private String idCategory, idProduct;
    private DatabaseReference mData;
    private RippleView rippleimgBackDetailProduct;
    private RecyclerView recyclerSize;
    private SizeProductAdapter adapter;
    private ArrayList<SizeProduct> arrSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        addControls ();
        getDataProduct ();
        addEvents ();
    }

    private void getDataProduct() {
        if (idProduct != null && idCategory != null){
            mData.child(com.threesome.shopme.LA.Constant.PRODUCTS_BY_CATEGORY).child(idCategory).child(idProduct).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        Product product = dataSnapshot.getValue(Product.class);
                        if (product != null){
                            txtProductName1.setText(product.getName().toString());
                            txtProductName2.setText(product.getName().toString());
                            txtProductName3.setText(product.getName().toString());
                            HashMap<String, Integer> mapSize = new HashMap<>();
                            mapSize = product.getMapSize();
                            Set set = mapSize.entrySet();
                            //Log.d("SET", set + "");
                            Iterator i = set.iterator();
                            // Hien thi cac phan tu
                            while(i.hasNext()) {
                                Map.Entry me = (Map.Entry)i.next();
                                arrSize.add(new SizeProduct(me.getKey().toString(), me.getValue().toString()));
                                adapter.notifyDataSetChanged();
                            }
                            int price = 0;
                            if (mapSize.containsKey("Big")){
                                price = mapSize.get("Big");
                            }else if (mapSize.containsKey("Normal")){
                                price = mapSize.get("Normal");
                            } else if (mapSize.containsKey("Small")){
                                price = mapSize.get("Small");
                            }
                            txtPriceProduct.setText(price + " VND");
                            txtPriceProductSmall.setText(price + " VND");
                            txtInfoProduct.setText(product.getDescription());
                            GlideApp.with(DetailProductActivity.this)
                                    .load(product.getImage())
                                    .centerCrop().into(imgProduct);
                            GlideApp.with(DetailProductActivity.this)
                                    .load(product.getImage())
                                    .centerCrop().into(imgProductSmall);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            Toast.makeText(this, "Lỗi định vị sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    private void addEvents() {
        txtBuyNow.setOnClickListener(this);
        imgProduct.setOnClickListener(this);
        layoutDetailProduct.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                collapseLayout();
                return true;
            }
        });
        rippleimgBackDetailProduct.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });
    }

    private void addControls() {

        Intent intent = getIntent();
        idCategory = intent.getStringExtra(Constant.ID_CATEGORY);
        idProduct = intent.getStringExtra(Constant.ID_PRODUCT);
        imgProduct = findViewById(R.id.imgProductDetail);
        layoutDetailProduct = findViewById(R.id.layoutDetailProduct);
        txtBuyNow = findViewById(R.id.txtBuyNow);
        cardViewProductImage = findViewById(R.id.cardViewProduct);
        recyclerSize = findViewById(R.id.recyclerSize);
        arrSize = new ArrayList<>();
        adapter = new SizeProductAdapter(DetailProductActivity.this, arrSize);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerSize.setLayoutManager(layoutManager);
        recyclerSize.setAdapter(adapter);


        txtProductName1 = findViewById(R.id.txtNameProductDetail);
        txtProductName2 = findViewById(R.id.txtNameProductDetail2);
        txtPriceProduct = findViewById(R.id.txtPriceProductDetail);
        txtInfoProduct = findViewById(R.id.txtInfoProductDetail);
        imgBack = findViewById(R.id.imgBackDetailProduct);
        imgLove = findViewById(R.id.imgLoveDetailProduct);
        layoutLove = findViewById(R.id.layoutLoveDetailproduct);
        layoutComment = findViewById(R.id.layoutCommentDetailProduct);
        imgAddToCart = findViewById(R.id.imgAddToCart);
        imgChat = findViewById(R.id.imgChatDetailProduct);
        rippleimgBackDetailProduct = findViewById(R.id.rippleimgBackDetailProduct);
        imgProductSmall = findViewById(R.id.imgProductSmall);
        txtProductName3 = findViewById(R.id.txtNameProductSmall);
        txtPriceProductSmall = findViewById(R.id.txtPriceProductDetailSmall);
        mData = FirebaseDatabase.getInstance().getReference();

        Display display = getWindowManager().getDefaultDisplay();
        String displayName = display.getName();  // minSdkVersion=17+
        Log.d("TAG", "displayName  = " + displayName);

        // display size in pixels
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.d("SIZE", size + "");
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
        params.setMargins(10, 10, 10, 0);
        cardViewProductImage.setLayoutParams(params);
        layout = (BottomSheetLayout) findViewById(R.id.bottom_sheet_layout);
        layout.toggle(); //collapses or expands the view according to the current state
        layout.collapse(); //collapses the view
        //Setup Spinner
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.txtBuyNow :
                buyNow ();
                break;
            case R.id.imgProductDetail :
                collapseLayout ();
                break;
        }
    }

    private void collapseLayout() {
        if (layout.isExpended()){
            layout.collapse();
        }
    }

    private void buyNow() {
        if (layout.isExpended()) {
            layout.collapse();
        }else {
            layout.expand();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        collapseLayout();
    }
}
