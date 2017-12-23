package com.threesome.shopme.LA;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.threesome.shopme.R;
import com.threesome.shopme.models.Product;

import java.io.File;

/**
 * Created by LanAnh on 20/12/2017.
 */

public class CreateProductFragment2 extends Fragment {
    private static final String TAG = CreateProductFragment2.class.getSimpleName();
    private CardView imgPhoto;
    private ImageView imgView, imgIcon;
    private Button btnReset, btnCreate;
    private static int IMG_RESULT =1;
    private static final int PICK_FROM_GALLERY = 1;
    private Product mProduct = new Product();
    private DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference productsByCategory = mData.child(Constant.PRODUCTS_BY_CATEGORY);
    private StorageReference mStorage;
    private Uri imageUri = null;
    private ProgressDialog progressDialog;

    public CreateProductFragment2(){}
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = FirebaseDatabase.getInstance().getReference();
        Bundle bundle = getArguments();
        String productStr = (String) bundle.get("product");
        mProduct = Utils.getGsonParser().fromJson(productStr, Product.class);
        Log.d(TAG, productStr);
        mStorage = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_product_2, container, false);
        imgPhoto = view.findViewById(R.id.imgPhoto);
        imgView = view.findViewById(R.id.imgView);
        imgIcon = view.findViewById(R.id.imgIcon);
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, IMG_RESULT);
                try {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btnReset = view.findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlideApp.with(getContext()).load("").placeholder(ContextCompat.getDrawable(getContext(), R.drawable.default_image)).into(imgView);
            }
        });
        btnCreate = view.findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, mProduct.getName());
                if(imageUri != null){
                    showProgress();
                    saveImageInStorage(imageUri);
                }
            }
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PICK_FROM_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode == IMG_RESULT && resultCode == getActivity().RESULT_OK && null != data){
                Uri uri = data.getData();
                String[] file = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(uri, file, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(file[0]);
                String imageDecode = cursor.getString(columnIndex);
                Log.d(TAG, imageDecode);
                imgIcon.setVisibility(View.GONE);
                File imgFile = new File(imageDecode);
                imageUri = Uri.fromFile(imgFile);
                Glide.with(getContext()).load(imageUri).into(imgView);
                cursor.close();
            }
        }catch (Exception ex){
            Toast.makeText(getContext(), "Please try again", Toast.LENGTH_SHORT).show();  }

    }
    public void saveImageInStorage(final Uri imageUri){
        final String id = productsByCategory.push().getKey();
        productsByCategory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProduct.setId(id);
                productsByCategory.child(mProduct.getCateId()).child(id).setValue(mProduct);
                StorageReference mImgProduct = mStorage.child(Constant.PRODUCTS_BY_CATEGORY).child(mProduct.getCateId());
                UploadTask uploadTask = mImgProduct.putFile(imageUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Create product failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        hideProgress();
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        final String imageUrl = String.valueOf(downloadUrl);
                        if(!imageUrl.isEmpty()){
                            productsByCategory.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    mProduct.setImage(imageUrl);
                                    productsByCategory.child(mProduct.getCateId()).child(id).child("image").setValue(imageUrl);
                                    Intent intent = new Intent(getActivity(), ListProductActivity.class);
                                    intent.putExtra("cateId", mProduct.getCateId());
                                    intent.putExtra("storeId", mProduct.getStoreId());
                                    getContext().startActivity(intent);
                                    getActivity().finish();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void showProgress (){
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void hideProgress (){
        progressDialog.hide();
    }
}
