package com.threesome.shopme.AT.product;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.qhutch.bottomsheetlayout.BottomSheetLayout;
import com.threesome.shopme.AT.cart.DetailCart;
import com.threesome.shopme.AT.cart.MyCart;
import com.threesome.shopme.AT.cart.UserCartActivity;
import com.threesome.shopme.AT.signIn.RequestSignInActivity;
import com.threesome.shopme.AT.singleton.FirebaseDB;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.LA.GlideApp;
import com.threesome.shopme.R;
import com.threesome.shopme.chat.ChatWithStoreActivity;
import com.threesome.shopme.models.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DetailProductActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cardViewProductImage;
    private ImageView imgProduct, imgProductSmall, imgLove, imgAddToCart, imgIncrease, imgDecrease;
    private TextView txtCountCart, txtAddToCart, txtNumberProduct, txtProductName1, txtProductName2, txtPriceProduct, txtInfoProduct, txtProductName3, txtPriceProductSmall;
    private BottomSheetLayout layout;
    private LinearLayout layoutDetailProduct, layoutLove, layoutComment;
    private FrameLayout layoutCart;
    private String idCategory, idProduct, idStore, idUser;
    private DatabaseReference mData;
    private RippleView rippleimgBackDetailProduct, ripplePayment, rippleChatDetailProduct, rippleDecrease, rippleIncrease;
    private RecyclerView recyclerSize;
    private SizeProductAdapter adapter;
    private ArrayList<SizeProduct> arrSize;
    private Product product;
    private int numberProduct = 1;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        layoutCart = findViewById(R.id.layoutCart);
        checkUserExits();
        addControls();
        getDataProduct();
        getCountProductInCart();
        addEvents();
    }

    private void getCountProductInCart() {
        if (idUser != null) {
            layoutCart.setVisibility(View.VISIBLE);
            mData.child(Constant.MYCART).child(idUser).child(idStore).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            txtCountCart.setText(dataSnapshot.getChildrenCount() + "");
                            txtCountCart.setVisibility(View.VISIBLE);
                        }
                    } else {
                        txtCountCart.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            layoutCart.setVisibility(View.INVISIBLE);
        }
    }

    private void getDataProduct() {
        if (idProduct != null && idCategory != null) {
            mData.child(com.threesome.shopme.LA.Constant.PRODUCTS_BY_CATEGORY).child(idCategory).child(idProduct).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        product = dataSnapshot.getValue(Product.class);
                        if (product != null) {
                            txtProductName1.setText(product.getName().toString());
                            txtProductName2.setText(product.getName().toString());
                            txtProductName3.setText(product.getName().toString());
                            HashMap<String, Integer> mapSize = new HashMap<>();
                            mapSize = product.getMapSize();
                            Set set = mapSize.entrySet();
                            //Log.d("SET", set + "");
                            Iterator i = set.iterator();
                            // Hien thi cac phan tu
                            while (i.hasNext()) {
                                Map.Entry me = (Map.Entry) i.next();
                                arrSize.add(new SizeProduct(me.getKey().toString(), me.getValue().toString()));
                                adapter.notifyDataSetChanged();
                            }
                            int price = 0;
                            if (mapSize.containsKey("Big")) {
                                price = mapSize.get("Big");
                            } else if (mapSize.containsKey("Normal")) {
                                price = mapSize.get("Normal");
                            } else if (mapSize.containsKey("Small")) {
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
        } else {
            Toast.makeText(this, "Lỗi định vị sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    private void addEvents() {
        txtAddToCart.setOnClickListener(this);
        imgAddToCart.setOnClickListener(this);
        imgProduct.setOnClickListener(this);
        imgDecrease.setOnClickListener(this);
        imgIncrease.setOnClickListener(this);

        //Collapse BottomSheet
        layoutDetailProduct.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                collapseLayout();
                return true;
            }
        });

        //Back
        rippleimgBackDetailProduct.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        //Payment all cart
        ripplePayment.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(DetailProductActivity.this, UserCartActivity.class);
                intent.putExtra(Constant.ID_STORE, idStore);
                intent.putExtra(Constant.ID_PRODUCT, idProduct);
                startActivity(intent);
            }
        });
        //Chat with store
        rippleChatDetailProduct.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                moveToChatBox();

            }
        });
        //Decrease and Increase Product
        rippleDecrease.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

            }
        });
        rippleIncrease.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

            }
        });
    }

    private void moveToChatBox() {
        if (idUser != null) {
            Intent intent = new Intent(DetailProductActivity.this, ChatWithStoreActivity.class);
            intent.putExtra(Constant.ID_STORE, idStore);
            intent.putExtra(Constant.ID_USER, idUser);
            intent.putExtra(Constant.ID_USERORSTORE, Constant.CODE_USER);
            startActivity(intent);
        } else {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailProductActivity.this);
            mBuilder.setMessage("Please Login To OrderStore!");
            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(DetailProductActivity.this, RequestSignInActivity.class));
                }
            });
            mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            mBuilder.show();
        }
    }

    private void addControls() {

        Intent intent = getIntent();
        idCategory = intent.getStringExtra(Constant.ID_CATEGORY);
        idProduct = intent.getStringExtra(Constant.ID_PRODUCT);
        idStore = intent.getStringExtra(Constant.ID_STORE);
        imgProduct = findViewById(R.id.imgProductDetail);
        layoutDetailProduct = findViewById(R.id.layoutDetailProduct);
        txtNumberProduct = findViewById(R.id.txtNumberProduct);
        cardViewProductImage = findViewById(R.id.cardViewProduct);
        recyclerSize = findViewById(R.id.recyclerSize);
        arrSize = new ArrayList<>();
        adapter = new SizeProductAdapter(DetailProductActivity.this, arrSize, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerSize.setLayoutManager(layoutManager);
        recyclerSize.setAdapter(adapter);
        progressDialog = new ProgressDialog(this);


        txtAddToCart = findViewById(R.id.txtAddToCart);
        txtCountCart = findViewById(R.id.txtCountCart);
        ripplePayment = findViewById(R.id.ripplePayment);
        rippleDecrease = findViewById(R.id.rippleDecrease);
        rippleIncrease = findViewById(R.id.rippleIncrease);
        rippleChatDetailProduct = findViewById(R.id.rippleChatDetailProduct);
        txtProductName1 = findViewById(R.id.txtNameProductDetail);
        txtProductName2 = findViewById(R.id.txtNameProductDetail2);
        txtPriceProduct = findViewById(R.id.txtPriceProductDetail);
        txtInfoProduct = findViewById(R.id.txtInfoProductDetail);
        imgIncrease = findViewById(R.id.imgIncrease);
        imgDecrease = findViewById(R.id.imgDecrease);
        imgLove = findViewById(R.id.imgLoveDetailProduct);
        layoutLove = findViewById(R.id.layoutLoveDetailproduct);
        layoutComment = findViewById(R.id.layoutCommentDetailProduct);
        imgAddToCart = findViewById(R.id.imgAddToCart);
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
        switch (id) {
            case R.id.imgAddToCart:
                expandedLayoutAddToCart();
                break;
            case R.id.imgProductDetail:
                collapseLayout();
                break;
            case R.id.imgIncrease:
                increaseProduct();
                break;
            case R.id.imgDecrease:
                decreaseProduct();
                break;
            case R.id.txtAddToCart:
                addToCart();
                break;
        }
    }

    private void addToCart() {

        if (idUser != null) {
            showProgress("Loading...");
            final SizeProduct sizeProduct = adapter.getSizeProductSelected();
            String hour = new SimpleDateFormat("HH").format(new java.util.Date());
            String minutes = new SimpleDateFormat("mm").format(new java.util.Date());
            String date = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
            final String currentTime = hour + " giờ " + minutes + " phút " + "ngày " + date;
            final ArrayList<DetailCart> arrDetailCart = new ArrayList<>();
            arrDetailCart.add(new DetailCart(sizeProduct.getNameSize(), sizeProduct.getPriceProduct(), currentTime, numberProduct));
            // arrDetailCart.add(new DetailCart(sizeProduct.getNameSize(), sizeProduct.getPriceProduct(), currentTime, numberProduct));

            //Check Product in Cart
            mData.child(Constant.MYCART).child(idUser).child(idStore).child(idProduct).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        MyCart myCart = dataSnapshot.getValue(MyCart.class);
                        ArrayList<DetailCart> arrCart = myCart.getArrDetailCart();
                        if (arrCart != null) {
                            boolean flagSize = false;
                            int indexSize = 0;
                            /**
                             * Check size on cart
                             */
                            for (int i = 0; i < arrCart.size(); i++) {
                                if (arrCart.get(i).getNameSize().equals(sizeProduct.getNameSize())) {
                                    flagSize = true;
                                    indexSize = i;
                                }
                            }
                            if (flagSize) {
                                //arrCart.remove(indexSize);
                                arrCart.set(indexSize, new DetailCart(sizeProduct.getNameSize(), sizeProduct.getPriceProduct(), currentTime, numberProduct + arrCart.get(indexSize).getCount()));
                                //arrCart.add(new DetailCart(sizeProduct.getNameSize(), sizeProduct.getPriceProduct(), currentTime, numberProduct + arrCart.get(indexSize).getCount()));
                            } else {
                                arrCart.add(new DetailCart(sizeProduct.getNameSize(), sizeProduct.getPriceProduct(), currentTime, numberProduct));
                            }
                            createNewProductInCart(arrCart);
                        }
                    } else {
                        createNewProductInCart(arrDetailCart);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailProductActivity.this);
            mBuilder.setMessage("Please Login To OrderStore!");
            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(DetailProductActivity.this, RequestSignInActivity.class));
                }
            });
            mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            mBuilder.show();
        }
    }

    private void createNewProductInCart(ArrayList<DetailCart> arrDetailCart) {
        int sumPrice = 0;
        for (int i = 0; i < arrDetailCart.size(); i++) {
            sumPrice += Integer.parseInt(arrDetailCart.get(i).getPrice()) * arrDetailCart.get(i).getCount();
        }
        MyCart myCart = new MyCart(idStore, product.getCateId(), product.getId(), product.getName(), product.getImage(), sumPrice, arrDetailCart);
        mData.child(Constant.MYCART).child(idUser).child(idStore).child(idProduct).setValue(myCart);
        collapseLayout();
        hideProgress();
        Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();

    }

    private void decreaseProduct() {
        if (numberProduct == 1) {
            Toast.makeText(this, "You have minximum Product!", Toast.LENGTH_SHORT).show();
            imgDecrease.setImageResource(R.drawable.ic_min_minus);
        } else {
            numberProduct--;
            if (numberProduct == 1)
                imgDecrease.setImageResource(R.drawable.ic_min_minus);
            else {
                imgDecrease.setImageResource(R.drawable.ic_decrease);
            }
        }
        txtNumberProduct.setText(numberProduct + "");
    }

    private void increaseProduct() {
        numberProduct++;
        imgDecrease.setImageResource(R.drawable.ic_decrease);
        txtNumberProduct.setText(numberProduct + "");
    }

    private void collapseLayout() {
        if (layout.isExpended()) {
            layout.collapse();
        }
    }

    private void expandedLayoutAddToCart() {
        if (layout.isExpended()) {
            layout.collapse();
        } else {
            layout.expand();
        }
    }

    @Override
    public void onBackPressed() {
        if(layout.isExpended())
            collapseLayout();
        else
            super.onBackPressed();
    }

    public void setPrice(String price) {
        txtPriceProductSmall.setText(price);
    }

    private void showProgress(String s) {
        progressDialog.setCancelable(false);
        progressDialog.setMessage(s);
        progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserExits();
    }

    private void checkUserExits() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            idUser = currentUser.getUid();
            layoutCart.setVisibility(View.VISIBLE);
        } else {
           // Toast.makeText(this, "You have Sign out", Toast.LENGTH_SHORT).show();
            layoutCart.setVisibility(View.INVISIBLE);
        }
    }
}
