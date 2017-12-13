package com.threesome.shopme.AT.createstore;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.R;

public class SecondFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private String nameStore = "";
    private String phoneStore = "";
    private String addressStore = "";
    private EditText edtNameStore, edtAddress, edtPhoneNumber;

    public SecondFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nameStore = getArguments().getString(Constant.STORE_NAME);
            phoneStore = getArguments().getString(Constant.STORE_PHONENUMBER);
            addressStore = getArguments().getString(Constant.STORE_ADDRESS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        edtAddress = view.findViewById(R.id.edtAddressStore);
        edtNameStore = view.findViewById(R.id.edtNameStore);
        edtPhoneNumber = view.findViewById(R.id.edtPhoneNumber);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDataBack ();
    }

    private void setDataBack() {
        edtNameStore.setText(nameStore);
        edtAddress.setText(addressStore);
        edtPhoneNumber.setText(phoneStore);
    }
    public boolean isNext (){
        boolean flag = true;
        nameStore = edtNameStore.getText().toString();
        addressStore = edtAddress.getText().toString();
        phoneStore = edtPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(nameStore)) {
            edtNameStore.setError("Bắt buộc!");
            flag = false;
        }
        if (TextUtils.isEmpty(addressStore)) {
            edtAddress.setError("Bắt buộc!");
            flag = false;
        }
        if (TextUtils.isEmpty(phoneStore)) {
            edtPhoneNumber.setError("Bắt buộc!");
            flag = false;
        }
        return flag;
    }

    public String getNameStore() {
        return nameStore;
    }

    public String getPhoneStore() {
        return phoneStore;
    }

    public String getAddressStore() {
        return addressStore;
    }
}
