package com.threesome.shopme.AT.createstore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.R;

import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirstFragment extends Fragment {
    private String email = "", password  = "", confirmPassword = "";
    public EditText edtEmail, edtPassword, edtConfirmPassword;
    public FirstFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(Constant.STORE_EMAIL);
            password = getArguments().getString(Constant.PASSWORD);
            confirmPassword = getArguments().getString(Constant.CONFIRM_PASSWORD);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDataBack ();
    }

    private void setDataBack() {
        edtEmail.setText(email);
        edtPassword.setText(password);
        edtConfirmPassword.setText(confirmPassword);
    }
    public boolean isNext (){
        boolean flag = true;
        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();
        confirmPassword = edtConfirmPassword.getText().toString();
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
            Toast.makeText(getContext(), "Mật khẩu không trùng khớp!!", Toast.LENGTH_SHORT).show();
            edtPassword.setText("");
            edtPassword.hasFocus();
            edtConfirmPassword.setText("");
        }
        if (password.length() > 0 && password.length() < 6) {
            flag = false;
            edtPassword.setText("");
            edtPassword.hasFocus();
            Toast.makeText(getContext(), "Mật khẩu phải có ít nhất 6 kí tự", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Mật khẩu phải có ít nhất 6 kí tự", Toast.LENGTH_SHORT).show();
        }
        return flag;
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

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
