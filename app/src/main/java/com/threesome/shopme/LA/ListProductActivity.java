package com.threesome.shopme.LA;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threesome.shopme.R;
import com.threesome.shopme.models.Product;

import java.util.ArrayList;

public class ListProductActivity extends AppCompatActivity {
    private static final String TAG = ListProductActivity.class.getSimpleName();
    private ImageView floatBtn, imgBack;
    private ProgressDialog progressDialog;
    private ArrayList<Product> mList = new ArrayList<>();
    private RecyclerView rvProduct;
    private com.threesome.shopme.AT.product.ProductAdapter adapter;
    private GridLayoutManager layoutManager;
    private DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference productsByCategory ;
    String mCateId = "", mStoreId = "";
    private EditText edtSearch;
    private ArrayList<String> nameList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);
        Intent intent = getIntent();
        if(intent.hasExtra("cateId")){
            mCateId = intent.getStringExtra("cateId");
            mStoreId = intent.getStringExtra("storeId");
            Log.d(TAG,mCateId + " "+mStoreId);
        }
        setCountProduct();
        progressDialog = new ProgressDialog(this);
        floatBtn = findViewById(R.id.btnAddProduct);
        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListProductActivity.this, CreateProductActivity.class);
                intent.putExtra("cateId", mCateId);
                intent.putExtra("storeId", mStoreId);
                startActivity(intent);
                finish();
            }
        });
        imgBack = findViewById(R.id.imgBackProductList);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rvProduct = findViewById(R.id.rvProduct);
        layoutManager = new GridLayoutManager(this, 3);
        rvProduct.setLayoutManager(layoutManager);
        adapter = new com.threesome.shopme.AT.product.ProductAdapter(mList, this, false, mStoreId);
        rvProduct.setAdapter(adapter);
        rvProduct.addOnItemTouchListener(new ItemTouchListener(this, rvProduct, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                showProductDialog(position);
            }
        }));
        edtSearch = findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()){
                    setAdapter(editable.toString());
                }
                if(editable.toString().isEmpty()){
                    new LoadDataTask().execute();
                }
            }
        });
    }

    private void setCountProduct() {
        if (mCateId != null){
            mData.child(Constant.PRODUCTS_BY_CATEGORY).child(mCateId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        final int countProduct = (int) dataSnapshot.getChildrenCount();
                        final DatabaseReference mChild = mData.child(Constant.CATEGORIES_BY_STORE).child(mStoreId).child(mCateId).child(Constant.QUANTITY);
                        mChild.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null){
                                    mChild.setValue(countProduct);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgress();
        new LoadDataTask().execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public void showProductDialog(final int position){
        final String id = mList.get(position).getId();
        CharSequence option[] = new CharSequence[]{"Edit","Remove"};
        final AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        dialogInterface.dismiss();
                        editNameDialog(position);
                        break;
                    case 1:
                        productsByCategory.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    Log.d("remove", String.valueOf(position));
                                    productsByCategory.child(id).removeValue();
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(ListProductActivity.this, "Product item has been removed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        break;
                }
            }
        });
        builder.show();
    }
    public void editNameDialog(final int position){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        final EditText edtName = dialogView.findViewById(R.id.edtName);
        final EditText edtPrice = dialogView.findViewById(R.id.edtPrice);
        final EditText edtDescription = dialogView.findViewById(R.id.edtDescription);
        edtName.setText(mList.get(position).getName());
        //edtPrice.setText(mList.get(position).getPrice());
        edtDescription.setText(mList.get(position).getDescription());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New product's name")
                .setView(dialogView)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(edtName.getText().toString().trim().equals("")||edtPrice.getText().toString().trim().equals("")
                                ||edtDescription.getText().toString().trim().equals("")){
                            edtName.setError("Invalid name");
                            Toast.makeText(ListProductActivity.this, "Invalid name.", Toast.LENGTH_SHORT).show();
                        }else{
                            mList.get(position).setName(edtName.getEditableText().toString());
                            productsByCategory.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot!= null){
                                        for(DataSnapshot data: dataSnapshot.getChildren()){
                                            Log.d("key",String.valueOf(data.getKey()));
                                            Log.d("key value", String.valueOf(data.getValue()));
                                            String id = mList.get(position).getId();
                                            productsByCategory.child(id).child("name").setValue(String.valueOf(edtName.getEditableText()));
                                            productsByCategory.child(id).child("price").setValue(String.valueOf(edtPrice.getEditableText()));
                                            productsByCategory.child(id).child("description").setValue(String.valueOf(edtDescription.getEditableText()));
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        dialog.show();
    }
    private void setAdapter(final String searchString){
        productsByCategory= mData.child(Constant.PRODUCTS_BY_CATEGORY).child(mCateId);
        productsByCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                nameList.clear();
                Log.d("datasnape", dataSnapshot.toString());
                if (!dataSnapshot.exists()) {

                } else {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String name = String.valueOf(data.child("name").getValue()).toLowerCase();
                        if(name.contains(searchString)){
                            Product product = data.getValue(Product.class);
                            mList.add(product);
                            Log.d(TAG, String.valueOf(data.child("name").getValue()));
                            //adapter.notifyDataSetChanged();
                        }
                    }
                }
                adapter = new com.threesome.shopme.AT.product.ProductAdapter(mList, getBaseContext(), false, mStoreId);
                rvProduct.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private class LoadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            productsByCategory= mData.child(Constant.PRODUCTS_BY_CATEGORY).child(mCateId);
            productsByCategory.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mList.clear();
                    Log.d("datasnape", dataSnapshot.toString());
                    if(!dataSnapshot.exists()){
                        publishProgress();
                    }else{
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            Product product = data.getValue(Product.class);
                            mList.add(product);
                            nameList.add(String.valueOf(data.child("name").getValue()).toLowerCase());
                            Log.d(TAG, String.valueOf(data.child("name").getValue()));
                            adapter.notifyDataSetChanged();
                            publishProgress();
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
            if(progressDialog.isShowing())
                hideProgress();
        }
    }
    private void showProgress (){
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void hideProgress (){
        progressDialog.hide();
    }
}
