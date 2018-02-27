package com.threesome.shopme.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.github.library.bubbleview.BubbleTextView;
import com.threesome.shopme.LA.GlideApp;
import com.threesome.shopme.R;
import com.threesome.shopme.chat.model.ChatMessage;
import com.threesome.shopme.AT.store.Store;
import com.threesome.shopme.models.User;
import com.threesome.shopme.AT.utility.Constant;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nhan on 1/9/2018.
 */

public class ChatMessageAdapter extends BaseAdapter{

    private List<ChatMessage> list_chat_model;
    private Context context;
    private LayoutInflater inflater;
    private Store store;
    private User user;
    private int isStoreOrUser;

    public ChatMessageAdapter(List<ChatMessage> list_chat_model, Context context , Store store, User user , int isStoreOrUser) {
        this.list_chat_model = list_chat_model;
        this.context = context;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.store = store;
        this.user = user;
        this.isStoreOrUser =isStoreOrUser;
    }

    @Override
    public int getCount() {
        if(list_chat_model == null)
            return 0;
        return list_chat_model.size();
    }

    @Override
    public Object getItem(int position) {
        return list_chat_model.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ChatMessage message = list_chat_model.get(position);
        boolean isSend = message.isSend();
        if(isStoreOrUser == Constant.CODE_USER)
            if(isSend){
                view = inflater.inflate(R.layout.item_message_chat_send,null);

            }
            else {
                view = inflater.inflate(R.layout.item_message_chat_recv, null);
            }
        else{
            if(!isSend){
                view = inflater.inflate(R.layout.item_message_chat_send,null);

            }
            else {
                view = inflater.inflate(R.layout.item_message_chat_recv, null);
            }
        }
        BubbleTextView txtTextMessage = view.findViewById(R.id.text_message);
        txtTextMessage.setText(list_chat_model.get(position).getMessageText());
        CircleImageView img = view.findViewById(R.id.imgAvatarMessage);
        if(list_chat_model.get(position).isSend()){
            if(user != null && !user.getAvatar().equals(""))
                GlideApp.with(context)
                        .load(user.getAvatar())
                        .centerCrop().into(img);
        }else{
            if(store != null && !store.getLinkPhotoStore().equals(""))
                GlideApp.with(context)
                        .load(store.getLinkPhotoStore())
                        .centerCrop().into(img);
        }
        return view;
    }

    public Store getStore() {
        return store;
    }

    public User getUser() {
        return user;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
