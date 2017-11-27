package com.threesome.shopme.create_store;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.TextView;

import com.threesome.shopme.R;

public class CreateStore_2Activity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtContinue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store_2);
        addControls();
        addEvetns();
        setupWindowAnimations();
    }
    private void addEvetns() {
        txtContinue.setOnClickListener(this);
    }

    private void addControls() {
        txtContinue = findViewById(R.id.txtContinue2);
    }
    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(3000);
        getWindow().setEnterTransition(fade);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.txtContinue2 :
                continueCreateStore ();
                break;
            default:
                break;
        }
    }

    private void continueCreateStore() {
        Intent intent = new Intent(CreateStore_2Activity.this, CreateStore_3Activity.class);
        startActivity(intent);
    }
}
