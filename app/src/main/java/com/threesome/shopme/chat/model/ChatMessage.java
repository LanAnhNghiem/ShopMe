package com.threesome.shopme.chat.model;

import android.widget.ImageView;

import java.util.Date;

/**
 * Created by Nhan on 1/9/2018.
 */

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private long messageTime;
    private boolean isSend;

    public ChatMessage() {
    }

    public ChatMessage(String messageText, String messageUser, boolean isSend) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.isSend = isSend;
        messageTime = new Date().getTime();
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
