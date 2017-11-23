package com.threesome.shopme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.threesome.shopme.create_store.CreateStoreActivity;

public class SidemenuActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgSidemenu;
    private DrawerLayout drawer;
    private TextView txtCreateStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sidemenu);
        addControls ();
        addEvents ();
    }

    private void addEvents() {
        imgSidemenu.setOnClickListener(this);
        txtCreateStore.setOnClickListener(this);
    }

    private void addControls() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        imgSidemenu = findViewById(R.id.imgSidemenu);
        txtCreateStore = findViewById(R.id.txtCreateStore);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.imgSidemenu :
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.txtCreateStore :
                createNewStore ();
                break;
            default:
                break;
        }
    }

    private void createNewStore() {
          startActivity(new Intent(SidemenuActivity.this, CreateStoreActivity.class));
    }
}
