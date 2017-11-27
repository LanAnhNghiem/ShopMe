package com.threesome.shopme.create_store;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.threesome.shopme.R;

public class CreateStore_3Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtContinue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creater_store_3);
        addControls();
        addEvetns();
    }
    private void addEvetns() {
        txtContinue.setOnClickListener(this);
    }

    private void addControls() {
        txtContinue = findViewById(R.id.txtComplete);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.txtComplete :
                continueCreateStore ();
                break;
            default:
                break;
        }
    }

    private void continueCreateStore() {
        Toast.makeText(this, "Register new store successful", Toast.LENGTH_SHORT).show();
    }
}
