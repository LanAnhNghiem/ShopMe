package com.threesome.shopme.AT.createstore;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.AT.utility.Utility;
import com.threesome.shopme.R;


public class CreateStore_2Activity extends Utility implements View.OnClickListener {

    private TextView txtContinue;
    private String email = "", idStore = "";
    private EditText edtName, edtPhoneNumber, edtAddress;
    private DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store_2);
        addControls();
        addEvetns();
    }

    private void addEvetns() {
        txtContinue.setOnClickListener(this);
    }

    private void addControls() {
        Intent intent = getIntent();
        email = intent.getStringExtra(Constant.STORE_EMAIL);
        idStore = intent.getStringExtra(Constant.ID_STORE);
        txtContinue = findViewById(R.id.txtContinue2);
        edtName = findViewById(R.id.edtNameStore);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtAddress = findViewById(R.id.edtAddressStore);

        mData = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View view) {
        showProgress("Loading...");
        int id = view.getId();
        switch (id) {
            case R.id.txtContinue2:
                continueCreateStore();
                break;
            default:
                break;
        }
    }

    private void continueCreateStore() {
        boolean flag = true;
        final String nameStore = edtName.getText().toString();
        final String phoneNumber = edtPhoneNumber.getText().toString();
        final String address = edtAddress.getText().toString();
        if (TextUtils.isEmpty(nameStore)) {
            flag = false;
            edtName.setError("Bắt buộc!");
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            flag = false;
            edtPhoneNumber.setError("Bắt buộc!");
        }
        if (TextUtils.isEmpty(address)) {
            flag = false;
            edtAddress.setError("Bắt buộc!");
        }
        if (flag) {
            hideProgress();
            Intent intent = new Intent(CreateStore_2Activity.this, CreateStore_3Activity.class);
            intent.putExtra(Constant.ID_STORE, idStore);
            intent.putExtra(Constant.STORE_EMAIL, email);
            intent.putExtra(Constant.STORE_ADDRESS, address);
            intent.putExtra(Constant.STORE_NAME, nameStore);
            intent.putExtra(Constant.STORE_PHONENUMBER, phoneNumber);
            startActivity(intent);
        } else {
            hideProgress();
        }

    }
}
