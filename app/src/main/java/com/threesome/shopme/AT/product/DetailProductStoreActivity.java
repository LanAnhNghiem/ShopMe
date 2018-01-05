package com.threesome.shopme.AT.product;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.qhutch.bottomsheetlayout.BottomSheetLayout;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.LA.GlideApp;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Product;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DetailProductStoreActivity extends AppCompatActivity {

    private TextView txtDeleteProduct, txtNameProduct1, txtNameProduct2, txtNameProduct3, txtPriceProduct1,
            txtPriceProduct2, txtPriceEdit, txtInfoProduct, txtBtnEditProductStore;
    private ImageView imgProduct, imgBackDetailProductStore, imgProductSmallStore, imgEditProduct, imgAddSizeProduct, imgEditNameProduct;
    private RippleView rippleimgBackDetailProductStore, rippleDeleteProduct, rippleEditNameProduct;
    private String idProduct, idCategory, linkCoverStore;
    private DatabaseReference mData;
    private ArrayList<SizeProduct> arrSize;
    private SizeProductAdapter adapter;
    private CardView cardViewProductStore;
    private BottomSheetLayout layout;
    private RecyclerView recyclerSize;
    private LinearLayout layoutDetailProductStore, layoutChangeImage;
    private ProgressDialog progressDialog;
    private Bitmap bitmap = null;
    private StorageReference mStorage;
    private ArrayList<String> arrSizeAdd;
    private HashMap<String, Integer> mapSize;
    private String sizeAdd[] = {"Big", "Normal", "Small"};
    private int index = 0; // 0 : Edit  1 : AddSzie  2 : Delete Size
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product_store);
        addControls ();
        getDataProduct ();
        addEvents ();
    }

    //Controls

    private void addControls() {
        mStorage = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        arrSizeAdd = new ArrayList<>();
        mapSize = new HashMap<>();
        rippleEditNameProduct = findViewById(R.id.rippleEditNameProduct);
        imgEditNameProduct = findViewById(R.id.imgEditNameProduct);
        rippleDeleteProduct= findViewById(R.id.rippleDeleteProduct);
        recyclerSize = findViewById(R.id.recyclerSizeStore);
        //txtPriceEdit = findViewById(R.id.txtPriceEdit);
        layoutChangeImage= findViewById(R.id.layoutChangeImageProduct);
        txtDeleteProduct = findViewById(R.id.txtDeleteProduct);
        imgAddSizeProduct = findViewById(R.id.imgAddSizeProduct);
        imgEditProduct = findViewById(R.id.imgEditSizeProduct);
        layoutDetailProductStore = findViewById(R.id.layoutDetailProductStore);
        txtBtnEditProductStore = findViewById(R.id.txtBtnEditProductStore);
        txtPriceProduct2 = findViewById(R.id.txtPriceProductDetailSmallStore);
        imgProductSmallStore= findViewById(R.id.imgProductSmallStore);
        layout = findViewById(R.id.bottom_sheet_layout_store);
        cardViewProductStore = findViewById(R.id.cardViewProductStore);
        mData = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        arrSize = new ArrayList<>();
        idCategory = intent.getStringExtra(Constant.ID_CATEGORY);
        idProduct = intent.getStringExtra(Constant.ID_PRODUCT);
        txtNameProduct1 = findViewById(R.id.txtNameProductDetailStore);
        txtNameProduct2 = findViewById(R.id.txtNameProductDetailStore2);
        txtNameProduct3 = findViewById(R.id.txtNameProductDetailStore3);
        txtPriceProduct1 = findViewById(R.id.txtPriceProductDetailStore);
        txtInfoProduct = findViewById(R.id.txtInfoProductDetailStore);
        imgProduct = findViewById(R.id.imgProductDetailStore);
        rippleimgBackDetailProductStore = findViewById(R.id.rippleimgBackDetailProductStore);
        imgBackDetailProductStore = findViewById(R.id.imgBackDetailProductStore);

        adapter = new SizeProductAdapter(this, arrSize, true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(DetailProductStoreActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerSize.setAdapter(adapter);
        recyclerSize.setLayoutManager(layoutManager);


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
        cardViewProductStore.setLayoutParams(params);
        layout.toggle(); //collapses or expands the view according to the current state
        layout.collapse(); //collapses the view


    }

    //Get Data of Product

    private void getDataProduct() {
        if (idProduct != null && idCategory != null){
            mData.child(com.threesome.shopme.LA.Constant.PRODUCTS_BY_CATEGORY).child(idCategory).child(idProduct).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        Product product = dataSnapshot.getValue(Product.class);
                        if (product != null){
                            txtNameProduct1.setText(product.getName().toString());
                            txtNameProduct2.setText(product.getName().toString());
                            txtNameProduct3.setText(product.getName().toString());
                            mapSize = product.getMapSize();
                            Set set = mapSize.entrySet();
                            //Log.d("SET", set + "");
                            Iterator i = set.iterator();
                            // Hien thi cac phan tu
                            arrSize.clear();
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
                            txtPriceProduct1.setText(price + " VND");
                            txtPriceProduct2.setText(price + " VND");
                           // txtPriceEdit.setText(price + " VND");
                            txtInfoProduct.setText(product.getDescription());
                            try {
                                GlideApp.with(DetailProductStoreActivity.this)
                                        .load(product.getImage())
                                        .centerCrop().into(imgProduct);
                                GlideApp.with(DetailProductStoreActivity.this)
                                        .load(product.getImage())
                                        .centerCrop().into(imgProductSmallStore);
                            }
                            catch (Exception ex){
                                ex.printStackTrace();
                            }
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

    //Events

    private void addEvents() {
        rippleimgBackDetailProductStore.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });
        layoutDetailProductStore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                collapseLayout();
                return true;
            }
        });
        imgEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expanded ();
                rippleEditNameProduct.setVisibility(View.VISIBLE);
                editProduct ();
            }
        });
        imgAddSizeProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expanded ();
                rippleEditNameProduct.setVisibility(View.GONE);
                addNewSizeProduct ();
            }
        });
        txtDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expanded ();
                rippleEditNameProduct.setVisibility(View.GONE);
                deleteProduct ();
            }
        });
        imgProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapseLayout();
            }
        });
        imgProductSmallStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage ();
            }
        });
        layoutChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage();
            }
        });
        txtBtnEditProductStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index == 0){
                    editProductOnFirebase ();
                }else if (index == 1){
                    addNewSizeProductOnFirebase ();
                }else if (index == 2){
                    deleteSizeProductOnFirebase ();
                }
            }
        });
        rippleDeleteProduct.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailProductStoreActivity.this);
                mBuilder.setMessage("Do you want to delete this Product?");
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showProgressDialog("Deleting...");
                        deleteAllSize ();
                    }
                });
                mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                mBuilder.show();
            }
        });
        rippleEditNameProduct.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

            }
        });
        imgEditNameProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editNameProduct();
            }
        });

    }

    //change Image

    private void changeImage() {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_PICK);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String pickTitle = "Take or select a photo";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
        startActivityForResult(chooserIntent, Constant.REQUEST_CODE_LOAD_IMAGE);
    }

    private void updateBanerStore (){
        if (bitmap != null) {
            showProgressDialog("Loading...");
            StorageReference mountainsRef = mStorage.child(com.threesome.shopme.LA.Constant.PRODUCTS_BY_CATEGORY).child(idCategory).child(idProduct);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] data = baos.toByteArray();
            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    hideProgress();
                    Toast.makeText(DetailProductStoreActivity.this, "Update baner Unsuccessfull", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    linkCoverStore = String.valueOf(downloadUrl);
                    if (!linkCoverStore.equals("")) {
                        final DatabaseReference mChild = mData.child(com.threesome.shopme.LA.Constant.PRODUCTS_BY_CATEGORY).child(idCategory).child(idProduct).child(com.threesome.shopme.LA.Constant.LINKIMAGEPRODUCT);
                        mChild.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null){
                                    mChild.setValue(linkCoverStore);
                                    imgProduct.setImageBitmap(bitmap);
                                    imgProductSmallStore.setImageBitmap(bitmap);
                                    hideProgress();
                                    Toast.makeText(DetailProductStoreActivity.this, "Update baner successfuly", Toast.LENGTH_SHORT).show();
                                }else {
                                    hideProgress();
                                    Toast.makeText(DetailProductStoreActivity.this, "Update baner Unsuccessfuly, Please check again!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                hideProgress();
                                Toast.makeText(DetailProductStoreActivity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    //Collapse and Expanded Bottomsheet

    private void expanded() {
        if (layout.isExpended()) {
            layout.collapse();
        }else {
            layout.expand();
        }
    }

    private void collapseLayout() {
        if (layout.isExpended()){
            layout.collapse();
        }
    }

    //Delete size

    private void deleteProduct() {
        index = 2;
        txtBtnEditProductStore.setBackgroundResource(R.drawable.bg_btn_delete);
        txtBtnEditProductStore.setText("Delete");
    }

    private void deleteSizeProductOnFirebase() {
        SizeProduct sizeProduct = adapter.getSizeProductSelected();
        mapSize.remove(sizeProduct.getNameSize());
        final DatabaseReference mDelete = mData.child(com.threesome.shopme.LA.Constant.PRODUCTS_BY_CATEGORY).child(idCategory).child(idProduct).child(Constant.MAPSIZE);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setMessage("Do you want to Delete Size?");
        mBuilder.setCancelable(false);
        mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDelete.setValue(mapSize);
                Set set = mapSize.entrySet();
                //Log.d("SET", set + "");
                Iterator b = set.iterator();
                // Hien thi cac phan tu
                arrSize.clear();
                while(b.hasNext()) {
                    Map.Entry me = (Map.Entry)b.next();
                    arrSize.add(new SizeProduct(me.getKey().toString(), me.getValue().toString()));
                }
                adapter.setArrSize(arrSize);
                adapter.setIndex(0);
                adapter.notifyDataSetChanged();
                collapseLayout();
                Toast.makeText(DetailProductStoreActivity.this, "Delete Size Successful!", Toast.LENGTH_SHORT).show();
            }
        });
        mBuilder.show();
    }

    private void deleteAllSize() {
        final DatabaseReference mProduct = mData.child(com.threesome.shopme.LA.Constant.PRODUCTS_BY_CATEGORY).child(idCategory).child(idProduct);
        mProduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    mProduct.removeValue();
                    hideProgress();
                    finish();
                    Toast.makeText(DetailProductStoreActivity.this, "Delete Successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Add new size

    private void addNewSizeProduct() {
        index = 1;
        txtBtnEditProductStore.setBackgroundResource(R.drawable.bg_btn_buy_now);
        txtBtnEditProductStore.setText("Create new size");
        arrSizeAdd.clear();
        arrSizeAdd.addAll(Arrays.asList(sizeAdd));
        if (arrSize.size() < 0) {
            Toast.makeText(this, "Error Load Type Of Product, Please try again!", Toast.LENGTH_SHORT).show();
        } else {
            if (arrSize.size() == 3) {

            } else if (arrSize.size() == 1 || arrSize.size() == 2) {
                for (int i = 0; i < arrSize.size(); i++) {
                    if (arrSize.get(i).getNameSize().equals("Big")) {
                        arrSizeAdd.remove(0);
                    }
                    if (arrSize.get(i).getNameSize().equals("Normal")) {
                        arrSizeAdd.remove(1);
                    }
                    if (arrSize.get(i).getNameSize().equals("Small")) {
                        arrSizeAdd.remove(2);
                    }
                }
            }
        }

    }

    private void addNewSizeProductOnFirebase() {

        if (arrSize.size() == 3){
            Toast.makeText(this, "You have full Size of Product, Please edit them!", Toast.LENGTH_SHORT).show();
        }else {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 0);
            params.gravity = Gravity.CENTER;
            TextView txtTitle = new TextView(this);
            txtTitle.setText("Choose Type Product");
            txtTitle.setTextSize(18);
            txtTitle.setTextColor(getResources().getColor(R.color.white));
            txtTitle.setLayoutParams(params);
            final Spinner spinner = new Spinner(this);
            final ArrayAdapter arrAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, arrSizeAdd);
            spinner.setAdapter(arrAdapter);
            spinner.setLayoutParams(params);
            spinner.setBackgroundResource(R.drawable.bg_spinner);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params1.setMargins(10, 10, 10, 0);
            params1.gravity = Gravity.CENTER;
            final EditText edtPrice = new EditText(this);
            edtPrice.setHint("Price VND");
            edtPrice.setTextSize(15);
            edtPrice.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            edtPrice.setLayoutParams(params1);
            linearLayout.addView(txtTitle);
            linearLayout.addView(spinner);
            linearLayout.addView(edtPrice);
            mBuilder.setView(linearLayout);
            mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    showProgressDialog("Loading...");
                    final DatabaseReference mSize = mData.child(com.threesome.shopme.LA.Constant.PRODUCTS_BY_CATEGORY).child(idCategory).child(idProduct).child(Constant.MAPSIZE);
                    mSize.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                mapSize.put(spinner.getSelectedItem().toString(), Integer.parseInt(edtPrice.getText().toString()));
                                mSize.setValue(mapSize);
                                collapseLayout();
                                Set set = mapSize.entrySet();
                                //Log.d("SET", set + "");
                                Iterator i = set.iterator();
                                // Hien thi cac phan tu
                                arrSize.clear();
                                while(i.hasNext()) {
                                    Map.Entry me = (Map.Entry)i.next();
                                    arrSize.add(new SizeProduct(me.getKey().toString(), me.getValue().toString()));
                                }
                                adapter.setArrSize(arrSize);
                                adapter.setIndex(0);
                                adapter.notifyDataSetChanged();
                                hideProgress();
                                Toast.makeText(DetailProductStoreActivity.this, "Add new size successful", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    Toast.makeText(DetailProductStoreActivity.this, "Create Successful", Toast.LENGTH_SHORT).show();
                }
            });
            mBuilder.setCancelable(false);
            mBuilder.show();
        }
    }

    //Edit Price Size And Name of Product

    private void editProduct() {
        index = 0;
        txtBtnEditProductStore.setBackgroundResource(R.drawable.bg_btn_buy_now);
        txtBtnEditProductStore.setText("Edit");
    }

    private void editProductOnFirebase() {

        final SizeProduct sizeProduct = adapter.getSizeProductSelected();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 0);
        params.gravity = Gravity.CENTER;
        TextView txtTitle = new TextView(this);
        txtTitle.setText("Change Product's Price");
        txtTitle.setTextSize(18);
        txtTitle.setTextColor(getResources().getColor(R.color.white));
        txtTitle.setLayoutParams(params);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.setMargins(10, 10, 10, 0);
        params1.gravity = Gravity.CENTER;
        final EditText edtNewPrice = new EditText(this);
        edtNewPrice.setHint("Enter new name");
        edtNewPrice.setTextSize(15);
        edtNewPrice.setText(sizeProduct.getPriceProduct().toString());
        edtNewPrice.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        edtNewPrice.setLayoutParams(params1);
        linearLayout.addView(txtTitle);
        linearLayout.addView(edtNewPrice);
        mBuilder.setView(linearLayout);
        mBuilder.setCancelable(false);
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newPrice = edtNewPrice.getText().toString();
                if (TextUtils.isEmpty(newPrice)){
                    Toast.makeText(DetailProductStoreActivity.this, "Error Empty Price!", Toast.LENGTH_SHORT).show();
                }else {
                    if (mapSize.containsKey(sizeProduct.getNameSize())){
                        mapSize.remove(sizeProduct.getNameSize());
                        mapSize.put(sizeProduct.getNameSize(), Integer.parseInt(edtNewPrice.getText().toString()));
                        mData.child(com.threesome.shopme.LA.Constant.PRODUCTS_BY_CATEGORY).child(idCategory).child(idProduct).child(Constant.MAPSIZE).setValue(mapSize);
                    }
                }
            }
        });
        mBuilder.show();
    }

    private void editNameProduct() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 0);
        params.gravity = Gravity.CENTER;
        TextView txtTitle = new TextView(this);
        txtTitle.setText("Change Name Product");
        txtTitle.setTextSize(18);
        txtTitle.setTextColor(getResources().getColor(R.color.white));
        txtTitle.setLayoutParams(params);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.setMargins(10, 10, 10, 0);
        params1.gravity = Gravity.CENTER;
        final EditText edtNewName = new EditText(this);
        edtNewName.setHint("Enter new name");
        edtNewName.setTextSize(15);
        edtNewName.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        edtNewName.setLayoutParams(params1);
        linearLayout.addView(txtTitle);
        linearLayout.addView(edtNewName);
        mBuilder.setView(linearLayout);
        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newName = edtNewName.getText().toString();
                if (TextUtils.isEmpty(newName)){
                    Toast.makeText(DetailProductStoreActivity.this, "Error Empty Name!!!", Toast.LENGTH_SHORT).show();
                }else {
                    mData.child(com.threesome.shopme.LA.Constant.PRODUCTS_BY_CATEGORY).child(idCategory).child(idProduct).child(com.threesome.shopme.LA.Constant.NAMEPRODUCT)
                            .setValue(newName);
                    Toast.makeText(DetailProductStoreActivity.this, "Change product name successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mBuilder.setCancelable(false);
        mBuilder.show();
    }

    //show and hide progressDialog
    private void showProgressDialog (String s){
        progressDialog.setMessage(s);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgress (){
        if (progressDialog != null){
            progressDialog.hide();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        collapseLayout();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data.getAction() != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                updateBanerStore();

            } else {
                Uri filePath = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    updateBanerStore();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    //setPrice of Size
    public void setPricce (String price){
        txtPriceProduct2.setText(price);
    }
}
