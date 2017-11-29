package com.threesome.shopme.create_store;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.threesome.shopme.utility.Constant;
import com.threesome.shopme.utility.Utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CreateStore_3Activity extends Utility implements View.OnClickListener {

    private TextView txtContinue;
    private String email = "", idStore = "", nameStore = "", address = "", phoneNumber = "", linkPhoto = "";
    private DatabaseReference mData;
    private StorageReference mStorageRef;
    private ImageView imgAvata, imgChooseImage;
    public static final int PICK_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creater_store_3);
        addControls();
        addEvetns();
    }
    private void addEvetns() {
        txtContinue.setOnClickListener(this);
        imgChooseImage.setOnClickListener(this);
    }

    private void addControls() {
        Intent intent = getIntent();
        email = intent.getStringExtra(Constant.STORE_EMAIL);
        idStore = intent.getStringExtra(Constant.ID_STORE);
        nameStore = intent.getStringExtra(Constant.STORE_NAME);
        address = intent.getStringExtra(Constant.STORE_ADDRESS);
        phoneNumber = intent.getStringExtra(Constant.STORE_PHONENUMBER);
        txtContinue = findViewById(R.id.txtComplete);
        imgAvata = findViewById(R.id.imgAvataStore);
        imgChooseImage = findViewById(R.id.imgChooseImage);
        mData = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.txtComplete :
                showProgress("Loading...");
                continueCreateStore(linkPhoto);
                break;
            case R.id.imgChooseImage :
                chooseImage ();
                break;
            default:
                break;
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }
    private void uploadImage (){
        StorageReference mountainImagesRef = mStorageRef.child(Constant.STORE).child(idStore).child("Avatar");
        // Get the data from an ImageView as bytes
        imgAvata.setDrawingCacheEnabled(true);
        imgAvata.buildDrawingCache();
        Bitmap bitmap = imgAvata.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                linkPhoto = downloadUrl.toString();
            }
        });
    }

    private void continueCreateStore(final String linkPhoto) {
        final DatabaseReference mRef = mData.child(Constant.STORE).child(idStore);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    Store store = new Store();
                    store.setIdStore(idStore);
                    store.setPhoneNumber(phoneNumber);
                    store.setNameStore(nameStore);
                    store.setEmailStore(email);
                    store.setAddressStore(address);
                    store.setLinkPhotoStore(linkPhoto);
                    mRef.setValue(store);
                    hideProgress();
                    Toast.makeText(CreateStore_3Activity.this, "Register new store successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgress();
                Toast.makeText(CreateStore_3Activity.this, databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            //TODO: action
            //linkPhoto = data.getData().toString();
            try {
                imgAvata.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadImage();
        }
    }

}
