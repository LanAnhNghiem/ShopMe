package com.threesome.shopme.chat;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.threesome.shopme.R;
import com.threesome.shopme.AT.utility.Constant;
import com.threesome.shopme.chat.model.ChatMessage;

public class ChatWithStoreActivity extends AppCompatActivity {

    private FloatingActionButton fabSend;
    private EditText edtMessage;
    private DatabaseReference mData , chatRef;
    private String idStore, idUser;
    private ListView listMessage;
    //private FirebaseListAdapter<ChatMessage> adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_store);
        mData = FirebaseDatabase.getInstance().getReference();
        idStore = getIntent().getStringExtra(Constant.ID_STORE);
        idUser = getIntent().getStringExtra(Constant.ID_USER);
        chatRef = mData.child(Constant.CHAT).child(idStore).child(idUser);
        addControls();
    }

    private void addControls() {
        edtMessage = findViewById(R.id.edtInputMessage);
        fabSend = findViewById(R.id.fabSend);
        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtMessage.getText().equals(""))
                    return;
                chatRef.push().setValue(new ChatMessage(edtMessage.getText().toString(), "NhanDuong", ""));
                edtMessage.setText("");
            }
        });
        listMessage = (ListView) findViewById(R.id.list_of_message);
//        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.item_message_chat, mData) {
//            @Override
//            protected void populateView(View v, ChatMessage model, int position) {
//                  ViewHoldeChatMessage holder = new ViewHoldeChatMessage();
//                  holder.message_text = findViewById(R.id.message_text);
//                  holder.message_user = findViewById(R.id.message_user);
//                  holder.message_time = findViewById(R.id.message_timer);
//
//                  holder.message_text.setText(model.getMessageText());
//                  holder.message_user.setText(model.getMessageUser());
//                  holder.message_time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss", model.getMessageTime()));
//            }
//        };
    }

//    public class ViewHoldeChatMessage{
//        public TextView message_user, message_text, message_time;
//    }
}
