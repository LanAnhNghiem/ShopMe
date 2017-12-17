package com.threesome.shopme.AT.signIn;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.threesome.shopme.AT.store.StoreDetailActivity;
import com.threesome.shopme.R;

public class SignInStoreActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtEmail, edtPassword;
    private ImageView imgSignIn;
    private TextView txtForgotPassword;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_store);
        addControls ();
        addEvents ();
    }

    private void addEvents() {
        imgSignIn.setOnClickListener(this);
        txtForgotPassword.setOnClickListener(this);
    }

    private void addControls(){
        mAuth = FirebaseAuth.getInstance();
        imgSignIn = findViewById(R.id.imgSignInStore);
        edtEmail = findViewById(R.id.edtEmailSignInStore);
        edtPassword = findViewById(R.id.edtPasswordSignInStore);
        txtForgotPassword = findViewById(R.id.txtForgotPasswordSignInStore);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imgSignInStore){
            signInStore ();

        }else if (id == R.id.txtForgotPasswordSignInStore){
            resetPassword ();
        }
    }

    private void signInStore() {
        boolean flag = true;
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        if (TextUtils.isEmpty(email)){
            flag = false;
            edtEmail.setError("Bắt buộc");
        }
        if (TextUtils.isEmpty(password)){
            flag = false;
            edtPassword .setError("Bắt buộc");
        }

        //Check Account Store
        if (flag){
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        startActivity(new Intent(SignInStoreActivity.this, StoreDetailActivity.class));
                    }else {
                        Toast.makeText(SignInStoreActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignInStoreActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void resetPassword() {
    }
}
