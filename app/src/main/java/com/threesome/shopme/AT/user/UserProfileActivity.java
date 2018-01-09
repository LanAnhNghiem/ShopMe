package com.threesome.shopme.AT.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.threesome.shopme.R;
import com.threesome.shopme.models.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    private CircleImageView imgAvatar;
    private TextView txtUsername, txtEmail;
    private ImageView btnBack;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Bitmap bitmap;
    private String idUser, linkPhotoUser;
    private DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    private final static int REQUEST_CODE_LOAD_IMAGE = 123;
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        addControl();
        getDataUser ();
    }
    private void addControl(){
        imgAvatar = findViewById(R.id.imgUser);
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePhotoUser();
            }
        });
        idUser = getUid();

    }
    public String getUid (){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            return user.getUid().toString();
        } else {
            return null;
            // User is signed out
        }
    }
    private void changePhotoUser() {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_PICK);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String pickTitle = "Choose avatar";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
        startActivityForResult(chooserIntent, REQUEST_CODE_LOAD_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data.getAction() != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                updatePhoto(bitmap, idUser);
                imgAvatar.setImageBitmap(bitmap);
            } else {
                Uri filePath = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    updatePhoto(bitmap, idUser);
                    imgAvatar.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    public void updatePhoto(Bitmap bitmap, final String idUser) {
        StorageReference mountainsRef = mStorage.child("Users").child(idUser);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(UserProfileActivity.this, "Update avatar successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                linkPhotoUser = String.valueOf(downloadUrl);
                if (!linkPhotoUser.equals("")) {
                    updateLinkPhotoUser(idUser, linkPhotoUser);
                }
            }
        });
    }
    public void updateLinkPhotoUser(String idUser, String linkPhotoUser) {
        mData.child("Users").child(idUser).child("avatar").setValue(linkPhotoUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UserProfileActivity.this, "Update avatar successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserProfileActivity.this, "Failed to update avatar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getDataUser() {
        if (idUser == null){
            imgAvatar.setEnabled(false);
        }
        else {
            imgAvatar.setEnabled(true);
            mData.child("Users").child(idUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null){
                            linkPhotoUser = user.getAvatar();
                            txtUsername.setText(user.getUserName());
                            txtEmail.setText(user.getEmail());

                            Glide.with(getApplicationContext())
                                    .load(linkPhotoUser)
                                    .into(imgAvatar);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
