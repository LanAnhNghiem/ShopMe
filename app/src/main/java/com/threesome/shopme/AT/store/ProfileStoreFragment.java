package com.threesome.shopme.AT.store;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.threesome.shopme.AT.createstore.RegisterStoreActivity;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.LA.GlideApp;
import com.threesome.shopme.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ProfileStoreFragment extends Fragment implements View.OnClickListener{

    private ImageView imgEditName, imgEditPhone, imgChangeBanner, imgBaner;
    private Bitmap bitmap = null;
    private TextView txtStoreName, txtStorePhoneNumber, txtStoreEmail, txtStoreAddress;
    private static final int PICK_IMAGE = 168;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private StorageReference mStorage;
    private ProgressDialog progressDialog;
    private String idStore, linkCoverStore;
    public ProfileStoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            idStore = getArguments().getString(Constant.ID_STORE);
        }
        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_store, container, false);
        addControls (view);
        setUpProfileStore ();
        return view;
    }

    private void setUpProfileStore() {
        if (idStore != null){
            mData.child(Constant.STORE).child(idStore).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        Store store = dataSnapshot.getValue(Store.class);
                        if (store.getNameStore() != null){
                            txtStoreName.setText(store.getNameStore());
                        }if (store.getPhoneNumber() != null){
                            txtStorePhoneNumber.setText(store.getPhoneNumber());
                        }if (store.getEmailStore() != null){
                            txtStoreEmail.setText(store.getEmailStore());
                        }if (store.getAddressStore() != null){
                            txtStoreAddress.setText(store.getAddressStore());
                        }if (store.getLinkPhotoStore() != null){
                            String link = store.getLinkPhotoStore();
                            GlideApp.with(getContext())
                                    .load(link)
                    //                .override(com.threesome.shopme.LA.Constant.PRODUCT_WIDTH, (int)(com.threesome.shopme.LA.Constant.PRODUCT_WIDTH/ com.threesome.shopme.LA.Constant.GOLDEN_RATIO))
                                    .centerCrop()
                                    .into(imgBaner);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void addControls(View view) {
        imgEditName = view.findViewById(R.id.imgEditNameStore);
        imgEditPhone = view.findViewById(R.id.imgEditPhoneStore);
        imgChangeBanner = view.findViewById(R.id.imgChangeBanner);
        imgBaner = view.findViewById(R.id.imgBanerStore);

        txtStoreName = view.findViewById(R.id.txtStoreName);
        txtStorePhoneNumber = view.findViewById(R.id.txtStorePhoneNumber);
        txtStoreEmail = view.findViewById(R.id.txtStoreEmail);
        txtStoreAddress = view.findViewById(R.id.txtStoreAddress);

        //Add Events
        imgEditName.setOnClickListener(this);
        imgEditPhone.setOnClickListener(this);
        imgChangeBanner.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imgEditNameStore){
            editStoreName ();
        }if (id == R.id.imgEditPhoneStore){
            editStorePhoneNumber ();
        }if (id == R.id.imgChangeBanner){
            changeBanerStore ();
        }
    }

    private void changeBanerStore() {
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_PICK);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String pickTitle = "Take or select a photo";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
        startActivityForResult(chooserIntent, Constant.REQUEST_CODE_LOAD_IMAGE);
    }

    private void editStorePhoneNumber() {
        final EditText edtNewPhoneNumber = new EditText(getContext());
        edtNewPhoneNumber.setHint("Enter your new phone number's store");
        LinearLayout layoutDialog = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(10, 0, 10, 0);
        params.gravity = Gravity.CENTER;
        edtNewPhoneNumber.setLayoutParams(params);
        layoutDialog.addView(edtNewPhoneNumber);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle("Change Store Name");
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newPhone = edtNewPhoneNumber.getText().toString();
                if (TextUtils.isEmpty(newPhone)){
                    edtNewPhoneNumber.setError("Bắt buộc!");
                    Toast.makeText(getContext(), "Empty store phone number, Please check again!", Toast.LENGTH_SHORT).show();
                }else {
                    showProgressDialog("Loading...");
                    changeStorePhoneNumberDB (newPhone);
                }
            }
        });
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mBuilder.setView(layoutDialog);
        mBuilder.show();
    }

    private void editStoreName() {
        final EditText edtNewName = new EditText(getContext());
        edtNewName.setHint("Enter your new name's store");
        LinearLayout layoutDialog = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(10, 0, 10, 0);
        params.gravity = Gravity.CENTER;
        edtNewName.setLayoutParams(params);
        layoutDialog.addView(edtNewName);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle("Change Store Name");
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newName = edtNewName.getText().toString();
                if (TextUtils.isEmpty(newName)){
                    edtNewName.setError("Bắt buộc!");
                    Toast.makeText(getContext(), "Empty store name, Please check again!", Toast.LENGTH_SHORT).show();
                }else {
                    showProgressDialog("Loading...");
                    changeStoreNameDB (newName);
                }
            }
        });
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mBuilder.setView(layoutDialog);
        mBuilder.show();

    }

    private void changeStorePhoneNumberDB(final String newPhone) {
        final DatabaseReference mChild = mData.child(Constant.STORE).child(idStore).child(Constant.STORE_PHONENUMBER);
        mChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    mChild.setValue(newPhone);
                    hideProgress();
                    Toast.makeText(getContext(), "Change Phonenumber's store successfully", Toast.LENGTH_SHORT).show();
                }else {
                    hideProgress();
                    Toast.makeText(getContext(), "Something is wrong, please check again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgress();
                Toast.makeText(getContext(), databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeStoreNameDB(final String newName) {
        final DatabaseReference mChild = mData.child(Constant.STORE).child(idStore).child(Constant.STORE_NAME);
        mChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    mChild.setValue(newName);
                    hideProgress();
                    Toast.makeText(getContext(), "Change name's store successfully", Toast.LENGTH_SHORT).show();
                }else {
                    hideProgress();
                    Toast.makeText(getContext(), "Something is wrong, please check again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgress();
                Toast.makeText(getContext(), databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showIcon (boolean flag){
        if (flag){
            imgEditName.setVisibility(View.VISIBLE);
            imgEditPhone.setVisibility(View.VISIBLE);
            imgChangeBanner.setVisibility(View.VISIBLE);
        }else {
            imgEditName.setVisibility(View.GONE);
            imgEditPhone.setVisibility(View.GONE);
            imgChangeBanner.setVisibility(View.GONE);
        }
    }

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_LOAD_IMAGE && resultCode == getActivity().RESULT_OK) {
            if (data.getAction() != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                updateBanerStore();

            } else {
                Uri filePath = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                    updateBanerStore();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private void updateBanerStore (){
        if (bitmap != null) {
            showProgressDialog("Loading...");
            StorageReference mountainsRef = mStorage.child(Constant.STORE).child(idStore);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] data = baos.toByteArray();
            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    hideProgress();
                    Toast.makeText(getContext(), "Update baner Unsuccessfull", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    linkCoverStore = String.valueOf(downloadUrl);
                    if (!linkCoverStore.equals("")) {
                        final DatabaseReference mChild = mData.child(Constant.STORE).child(idStore).child(Constant.LINKCBANERSTORE);
                        mChild.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null){
                                    mChild.setValue(linkCoverStore);
                                    imgBaner.setImageBitmap(bitmap);
                                    hideProgress();
                                    Toast.makeText(getContext(), "Update baner successfuly", Toast.LENGTH_SHORT).show();
                                }else {
                                    hideProgress();
                                    Toast.makeText(getContext(), "Update baner Unsuccessfuly, Please check again!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                hideProgress();
                                Toast.makeText(getContext(), databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }
}
