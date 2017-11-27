package com.threesome.shopme.create_store;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threesome.shopme.R;
import com.threesome.shopme.utility.Constant;
import com.threesome.shopme.utility.Utility;

public class CreateStoreActivity extends Utility implements View.OnClickListener{

    private TextView txtContinue;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private EditText edtEmail, edtPassword, edtConfirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store);
        addControls ();
        addEvetns ();
        setupWindowAnimations();
    }

    private void addEvetns() {
        txtContinue.setOnClickListener(this);
    }

    private void addControls() {
        txtContinue = findViewById(R.id.txtContinue1);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.txtContinue1 :
                continueCreateStore ();
                break;
            default:
                break;
        }
    }

    private void continueCreateStore() {
        boolean flag = true;
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(email)){
            edtEmail.setError("Bắt buộc!");
            flag = false;
        }
        if (TextUtils.isEmpty(password)){
            edtPassword.setError("Bắt buộc!");
            flag = false;
        }
        if (TextUtils.isEmpty(confirmPassword)){
            edtConfirmPassword.setError("Bắt buộc!");
            flag = false;
        }
        if (!password.equals(confirmPassword)){
            flag = false;
            Toast.makeText(this, "Mật khẩu không trùng khớp!!", Toast.LENGTH_SHORT).show();
            edtPassword.setText("");
            edtPassword.hasFocus();
            edtConfirmPassword.setText("");
        }
        if (password.length() > 0 && password.length() < 6){
            flag = false;
            edtPassword.setText("");
            edtPassword.hasFocus();
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 kí tự", Toast.LENGTH_SHORT).show();
        }
        if (!isEmailVail(email)){
            flag = false;
            edtEmail.hasFocus();
            edtEmail.setError("Email không hợp lệ!");
        }
        if (confirmPassword.length() > 0 && confirmPassword.length() < 6){
            flag = false;
            edtConfirmPassword.setText("");
            edtConfirmPassword.hasFocus();
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 kí tự", Toast.LENGTH_SHORT).show();
        }
        if (flag) {
           createNewStore (email, password);
        }
    }

    private void createNewStore(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(" TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //addToDataBase ();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateStoreActivity.this, "Đăng kí thất bại!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateStoreActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToDataBase() {
        final DatabaseReference myRef = mData.child(Constant.STORE);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Store store = new Store();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupWindowAnimations() {
        Slide slide = new Slide();
        slide.setDuration(3000);
        getWindow().setExitTransition(slide);
    }
}
