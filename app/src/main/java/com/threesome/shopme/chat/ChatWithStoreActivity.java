package com.threesome.shopme.chat;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.threesome.shopme.R;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.chat.adapter.ChatMessageAdapter;
import com.threesome.shopme.chat.model.ChatMessage;
import com.threesome.shopme.AT.store.Store;
import com.threesome.shopme.models.User;

import java.util.ArrayList;
import java.util.List;

public class ChatWithStoreActivity extends AppCompatActivity {

    private FloatingActionButton fabSend;
    private EditText edtMessage;
    private DatabaseReference mData , chatRef;
    private String idStore, idUser;
    private int isStoreOrUser;
    private Store store;
    private User user;
    private ListView listMessage;
    private List<ChatMessage> messages;
    private ChatMessageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_store);
        mData = FirebaseDatabase.getInstance().getReference();
        idStore = getIntent().getStringExtra(Constant.ID_STORE);
        idUser = getIntent().getStringExtra(Constant.ID_USER);
        chatRef = mData.child(Constant.CHAT).child(idStore).child(idUser);
        isStoreOrUser = getIntent().getIntExtra(Constant.ID_USERORSTORE, -1);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mData.child(Constant.STORE).child(idStore).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null){
                            store = dataSnapshot.getValue(Store.class);
                            adapter.setStore(store);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mData.child(Constant.USER).child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null){
                            user = dataSnapshot.getValue(User.class);
                            adapter.setUser(user);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        addControls();
    }

    private void addControls() {
        messages = new ArrayList<>();
        edtMessage = findViewById(R.id.edtInputMessage);
        fabSend = findViewById(R.id.fabSend);
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtMessage.getText().equals(""))
                    return;
                else{
                    if(isStoreOrUser == Constant.CODE_USER){
                        chatRef.push().setValue(new ChatMessage(edtMessage.getText().toString(), "NhanDuong", true));
                    }
                    else{
                        chatRef.push().setValue(new ChatMessage(edtMessage.getText().toString(), "NhanDuong", false));
                    }
                    edtMessage.setText("");
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        listMessage = findViewById(R.id.list_of_message);
        adapter = new ChatMessageAdapter(messages, this , store , user, isStoreOrUser);
        listMessage.setAdapter(adapter);
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messages.clear();
                if(dataSnapshot.getValue() != null){
                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        ChatMessage message = item.getValue(ChatMessage.class);
                        messages.add(message);
                        adapter.notifyDataSetChanged();
                        listMessage.setSelection(listMessage.getAdapter().getCount()-1);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
