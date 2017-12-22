package com.threesome.shopme.LA;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.threesome.shopme.R;
import com.threesome.shopme.models.Product;

/**
 * Created by LanAnh on 20/12/2017.
 */

public class CreateProductFragment1 extends Fragment {
    public static final String TAG = CreateProductFragment1.class.getSimpleName();
    EditText txtProductName, txtPrice, txtDescription;
    Button btnReset, btnContinue;
    String mCateId="";
    String mStoreId = "";
    SendProductData sendProductData;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendProductData = (SendProductData) getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_product_1, container, false);
        txtProductName = view.findViewById(R.id.txtProductName);
        txtPrice = view.findViewById(R.id.txtPrice);
        txtDescription = view.findViewById(R.id.txtDescription);
        btnReset = view.findViewById(R.id.btnReset);
        btnContinue = view.findViewById(R.id.btnContinue);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtProductName.setText("");
                txtDescription.setText("");
                txtPrice.setText("");
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtDescription.getText().toString().trim().length() > 0&& txtProductName.getText().toString().trim().length() > 0
                        && txtPrice.getText().toString().trim().length() > 0){
                    saveData();
                }else{
                    Toast.makeText(getActivity(), "Please fill in the blanks", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    public void saveData(){
        Log.d(TAG, String.valueOf(txtPrice.getEditableText()));
        Product product = new Product(txtProductName.getEditableText().toString(), String.valueOf(txtPrice.getEditableText()),txtDescription.getEditableText().toString());
        sendProductData.getData(product);
    }
}
