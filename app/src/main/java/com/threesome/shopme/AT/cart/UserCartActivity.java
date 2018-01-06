package com.threesome.shopme.AT.cart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.CustomMapsActivity;
import com.threesome.shopme.R;

import java.util.ArrayList;

public class UserCartActivity extends AppCompatActivity {

    private RippleView rippleBack;
    private RecyclerView recyclerProduct;
    private ArrayList<MyCart> arrMyCart;
    private MyCartAdapter adapter;
    private String idStore, idProduct, idUser;
    private TextView txtCountCart;
    private DatabaseReference mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cart);
        checkUserExits ();
        addControls ();
        getDataMyCart ();
        addEvents ();
    }

    private void addEvents() {
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });
    }

    private void getDataMyCart() {
        mData.child(Constant.MYCART).child(idUser).child(idStore).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrMyCart.clear();
                if (dataSnapshot.getValue() != null){
                    txtCountCart.setVisibility(View.VISIBLE);
                    txtCountCart.setText(dataSnapshot.getChildrenCount() + "");
                    for (DataSnapshot dt : dataSnapshot.getChildren()){
                        MyCart myCart = dt.getValue(MyCart.class);
                        arrMyCart.add(myCart);
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    txtCountCart.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addControls() {

        Intent intent = getIntent();
        idProduct = intent.getStringExtra(Constant.ID_PRODUCT);
        idStore = intent.getStringExtra(Constant.ID_STORE);
        mData = FirebaseDatabase.getInstance().getReference();
        arrMyCart = new ArrayList<>();
        recyclerProduct = findViewById(R.id.recyclerProduct);
        adapter = new MyCartAdapter(arrMyCart, this, idUser);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerProduct.setLayoutManager(layoutManager);
        recyclerProduct.setAdapter(adapter);

        rippleBack = findViewById(R.id.rippleimgBackDetailProduct);
        txtCountCart = findViewById(R.id.txtCountCart2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserExits();
    }

    private void checkUserExits() {
        FirebaseAuth mAuth = null;
        if (mAuth == null){
             mAuth = FirebaseAuth.getInstance();
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            idUser = currentUser.getUid();
        }else {
            startActivity(new Intent(UserCartActivity.this, CustomMapsActivity.class));
            Toast.makeText(this, "You have Logged out", Toast.LENGTH_SHORT).show();
        }
    }
}
