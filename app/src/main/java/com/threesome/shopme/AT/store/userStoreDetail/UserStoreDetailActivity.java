package com.threesome.shopme.AT.store.userStoreDetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.LA.SearchFragment;
import com.threesome.shopme.R;

public class UserStoreDetailActivity extends AppCompatActivity {

    private String idStore = "";
    private EditText edtSearchProductUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_store_detail);
        Intent intent = getIntent();
        idStore = intent.getStringExtra(Constant.ID_STORE);
        edtSearchProductUser = findViewById(R.id.edtSearchProductUser);
        edtSearchProductUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()){
                    addSearchFragment(editable.toString());
                }else{
                    addStoreDetailFragment();
                }
            }
        });
        addStoreDetailFragment();
    }
    public void addStoreDetailFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        UserStoreDetailFragment fragmentCategory = new UserStoreDetailFragment();
        fragmentTransaction.replace(R.id.container, fragmentCategory);
        fragmentTransaction.commit();
    }
    public void addSearchFragment(String search){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        UserSearchFragment fragmentSearch = new UserSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("search", search);
        bundle.putString(Constant.ID_STORE, idStore);
        fragmentSearch.setArguments(bundle);
        fragmentTransaction.replace(R.id.container, fragmentSearch);
        fragmentTransaction.commit();
    }
}
