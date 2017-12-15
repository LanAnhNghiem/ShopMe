package com.threesome.shopme.AT.createstore;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.AT.utility.Utility;
import com.threesome.shopme.R;

public class CreateStoreActivity extends Utility implements View.OnClickListener {

    private TextView txtContinue;
    private FirebaseAuth mAuth;
    private EditText edtEmail, edtPassword, edtConfirmPassword;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store);
        addControls();
        addEvetns();
        setupWindowAnimations();
    }

    private void addEvetns() {
        txtContinue.setOnClickListener(this);
    }

    private void addControls() {
        txtContinue = (TextView)findViewById(R.id.txtContinue1);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        showProgress("Loading...");
        int id = view.getId();
        switch (id) {
            case R.id.txtContinue1:
                continueCreateStore();
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
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Bắt buộc!");
            flag = false;
        }
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Bắt buộc!");
            flag = false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            edtConfirmPassword.setError("Bắt buộc!");
            flag = false;
        }
        if (!password.equals(confirmPassword)) {
            flag = false;
            Toast.makeText(this, "Mật khẩu không trùng khớp!!", Toast.LENGTH_SHORT).show();
            edtPassword.setText("");
            edtPassword.hasFocus();
            edtConfirmPassword.setText("");
        }
        if (password.length() > 0 && password.length() < 6) {
            flag = false;
            edtPassword.setText("");
            edtPassword.hasFocus();
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 kí tự", Toast.LENGTH_SHORT).show();
        }
        if (!isEmailVail(email)) {
            flag = false;
            edtEmail.hasFocus();
            edtEmail.setError("Email không hợp lệ!");
        }
        if (confirmPassword.length() > 0 && confirmPassword.length() < 6) {
            flag = false;
            edtConfirmPassword.setText("");
            edtConfirmPassword.hasFocus();
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 kí tự", Toast.LENGTH_SHORT).show();
        }
        if (flag) {
            createNewStore(email, password);
        } else {
            hideProgress();
        }
    }

    private void createNewStore(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            hideProgress();
                            Log.d(" TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(CreateStoreActivity.this, CreateStore_2Activity.class);
                            intent.putExtra(Constant.STORE_EMAIL, email);
                            intent.putExtra(Constant.PASSWORD, password);
                            intent.putExtra(Constant.ID_STORE, user.getUid());
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            hideProgress();
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateStoreActivity.this, "Đăng kí thất bại!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgress();
                Toast.makeText(CreateStoreActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Slide slide = new Slide();
        slide.setDuration(3000);
        getWindow().setExitTransition(slide);
    }

}
