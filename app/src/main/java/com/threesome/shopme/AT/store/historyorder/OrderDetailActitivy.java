package com.threesome.shopme.AT.store.historyorder;

import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.threesome.shopme.AT.cart.MyCart;
import com.threesome.shopme.AT.cart.OrderStore;
import com.threesome.shopme.AT.utility.Constant;

import com.threesome.shopme.LA.GlideApp;
import com.threesome.shopme.R;

import java.util.ArrayList;

public class OrderDetailActitivy extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<MyCart> myCarts = null;
    private OrderStore orderStore = null;
    private MyCartStoreAdapter adapter;
    private RecyclerView recyclerOrderList;
    private TextView txtNhanDon, txtHuy, txtNameUserOrder, txtTimeOrder , txtStatusUserOrder;
    private ImageView imgUserOrder;
    private DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_actitivy);
        orderStore = (OrderStore) getIntent().getSerializableExtra(Constant.CODE_SEND_ORDER);
        if(orderStore != null){
            myCarts = orderStore.getMyCarts();
        }
        addControls();

    }

    private void addControls() {
        mData = FirebaseDatabase.getInstance().getReference();
        txtNhanDon = findViewById(R.id.txtNhanDon);
        txtHuy = findViewById(R.id.txtHuyDon);
        txtNhanDon.setOnClickListener(this);
        txtHuy.setOnClickListener(this);
        recyclerOrderList = findViewById(R.id.recyclerOrderDetailList);
        adapter = new MyCartStoreAdapter(myCarts, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerOrderList.setLayoutManager(layoutManager);
        recyclerOrderList.setAdapter(adapter);

        txtStatusUserOrder = findViewById(R.id.txtStatusOrderStore);
        txtNameUserOrder = findViewById(R.id.txtNameUserOrder);
        imgUserOrder = findViewById(R.id.imgAvatarUserOrder);
        txtTimeOrder = findViewById(R.id.txtTimeOrder);
        GlideApp.with(this)
                .load(orderStore.getCustomer().getAvatarUser())
                .centerCrop().into(imgUserOrder);


        txtNameUserOrder.setText(orderStore.getCustomer().getName());
        HienThiLabel();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        DatabaseReference statusRef = mData.child(Constant.ORDERSTORE).child(orderStore.getIdStore()).child(orderStore.getIdOrderOfUser()).child(Constant.STATUS);
        if(id == R.id.txtNhanDon) {
            switch (orderStore.getStatus()){
                case Constant.CODE_CHUA_NHAN:
                    statusRef.setValue(Constant.CODE_DA_NHAN);
                    orderStore.setStatus(Constant.CODE_DA_NHAN);
                    HienThiLabel();
                    break;
                case Constant.CODE_DA_NHAN:
                    statusRef.setValue(Constant.CODE_SHIP);
                    orderStore.setStatus(Constant.CODE_SHIP);
                    HienThiLabel();
                    break;
                case Constant.CODE_SHIP:
                    statusRef.setValue(Constant.CODE_DA_GIAO);
                    orderStore.setStatus(Constant.CODE_DA_GIAO);
                    HienThiLabel();
                    break;
                default:
                    break;
            }
        }

        if(id == R.id.txtHuyDon){
            statusRef.setValue(Constant.CODE_DA_HUY);
            orderStore.setStatus(Constant.CODE_DA_HUY);
            HienThiLabel();
        }
    }

    private void HienThiLabel(){
        int status = orderStore.getStatus();
        switch (status){
            case Constant.CODE_CHUA_NHAN:
                txtNhanDon.setText("Nhận đơn");
                txtStatusUserOrder.setText("Chờ phê duyệt");
                break;
            case Constant.CODE_DA_NHAN:
                txtNhanDon.setText("Giao hàng");
                txtStatusUserOrder.setText("Đang tiến hành");
                break;
            case Constant.CODE_SHIP:
                txtNhanDon.setText("Đã giao");
                txtStatusUserOrder.setText("Đang giao hàng");
                break;
            case Constant.CODE_DA_GIAO:
                txtStatusUserOrder.setText("Đã giao");
                txtNhanDon.setVisibility(View.INVISIBLE);
                txtHuy.setVisibility(View.INVISIBLE);
                break;
            case Constant.CODE_DA_HUY:
                txtStatusUserOrder.setText("Đã hủy");
                txtNhanDon.setVisibility(View.INVISIBLE);
                txtHuy.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }
}
