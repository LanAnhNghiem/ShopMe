package com.threesome.shopme.AT.cart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threesome.shopme.AT.product.SizeProduct;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.CustomMapsActivity;
import com.threesome.shopme.NN.user.Customer;
import com.threesome.shopme.R;
import com.threesome.shopme.models.User;
import com.threesome.shopme.AT.store.Store;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class UserCartActivity extends AppCompatActivity {

    private RippleView rippleBack;
    private RecyclerView recyclerProduct;
    private ArrayList<MyCart> arrMyCart;
    private MyCartAdapter adapter;
    private String idStore, idProduct, idUser;
    private TextView txtCountCart, txtPayment;
    private DatabaseReference mData;
    private User user = null;
    private Store store = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cart);
        mData = FirebaseDatabase.getInstance().getReference();
        checkUserExits ();
        addControls ();
        getDataMyCart ();
        getDataStore();
        addEvents ();


    }

    private void getDataStore() {
        mData.child(Constant.STORE).child(idStore).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    store = dataSnapshot.getValue(Store.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addEvents() {
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });
        txtPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialogPayment();
            }
        });
    }

    private void displayDialogPayment() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setMessage("Do you want to pay this cart?");
        mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                paymentCart();
            }
        });
        mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mBuilder.setCancelable(false);
        mBuilder.show();
    }

    private void paymentCart() {
        if(user.getPhoneNumber() != null){
            Toast.makeText(this, "Please, update your phone number in user profile!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Day len store
        String hour = new SimpleDateFormat("HH").format(new java.util.Date());
        String minutes = new SimpleDateFormat("mm").format(new java.util.Date());
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
        final String currentTime = hour + " giờ " + minutes + " phút " + "ngày " + date;
        String idOrderOfUser = mData.child(Constant.ORDERSTORE).child(idStore).push().getKey();
        final DatabaseReference mOrderStore =  mData.child(Constant.ORDERSTORE).child(idStore).child(idOrderOfUser);
        final OrderStore orderStore = new OrderStore(new Customer(user.getUserName() , user.getPhoneNumber(), user.getAvatar()),arrMyCart, idStore , idOrderOfUser, currentTime, idUser);
        mOrderStore.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    mOrderStore.setValue(orderStore).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //Xoa node
                                mData.child(Constant.MYCART).child(idUser).child(idStore).removeValue();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Luu vao lich su user
        String idOrderToStore = mData.child(Constant.ORDERUSER).child(idUser).push().getKey();
        final DatabaseReference mOrderOfUser = mData.child(Constant.ORDERUSER).child(idUser).child(idOrderToStore);
        final OrderUser orderUser = new OrderUser(store.getIdStore(), store.getLinkPhotoStore(), store.getNameStore(), arrMyCart);
        mOrderOfUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    mOrderOfUser.setValue(orderUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        arrMyCart = new ArrayList<>();
        recyclerProduct = findViewById(R.id.recyclerProduct);
        adapter = new MyCartAdapter(arrMyCart, this, idUser);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerProduct.setLayoutManager(layoutManager);
        recyclerProduct.setAdapter(adapter);

        rippleBack = findViewById(R.id.rippleimgBackDetailProduct);
        txtCountCart = findViewById(R.id.txtCountCart2);

        txtPayment = findViewById(R.id.txtPayment);
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
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            idUser = currentUser.getUid();
            mData.child(Constant.USER).child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        user = dataSnapshot.getValue(User.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            startActivity(new Intent(UserCartActivity.this, CustomMapsActivity.class));
            Toast.makeText(this, "You have Logged out", Toast.LENGTH_SHORT).show();
        }
    }
}
