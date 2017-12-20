package com.threesome.shopme.AT.signIn;

import android.app.ProgressDialog;
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
import com.threesome.shopme.AT.store.ResetPasswordActivity;
import com.threesome.shopme.AT.store.StoreDetailActivity;
import com.threesome.shopme.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInStoreActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtEmail, edtPassword;
    private ImageView imgSignIn;
    private TextView txtForgotPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
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
        imgSignIn = findViewById(R.id.imgSignInStore2);
        edtEmail = findViewById(R.id.edtEmailSignInStore);
        edtPassword = findViewById(R.id.edtPasswordSignInStore);
        txtForgotPassword = findViewById(R.id.txtForgotPasswordSignInStore);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imgSignInStore2){
            signInStore ();

        }else if (id == R.id.txtForgotPasswordSignInStore){
            resetPassword ();
        }
    }

    private void signInStore() {
        showProgress("Loading...");
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
        if (!isEmailVail(email)){
            flag = false;
            edtEmail.setError("Email không hợp lệ!");
        }

        //Check Account Store
        if (flag){
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        hideProgress();
                        startActivity(new Intent(SignInStoreActivity.this, StoreDetailActivity.class));
                    }else {
                        hideProgress();
                        Toast.makeText(SignInStoreActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgress();
                    Toast.makeText(SignInStoreActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            hideProgress();
        }

    }

    private void resetPassword() {
        startActivity(new Intent(SignInStoreActivity.this, ResetPasswordActivity.class));
    }
    private void showProgress (String s){
        progressDialog.setMessage(s);
        progressDialog.show();
    }
    private void hideProgress (){
        progressDialog.hide();
    }
    public boolean isEmailVail(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
