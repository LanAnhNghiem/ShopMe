package com.threesome.shopme.chat.model;

import android.widget.ImageView;

import java.util.Date;

/**
 * Created by Nhan on 1/9/2018.
 */

public class ChatMessage {
    private String messageText;
    private String messageUser;
    private String linkAvatar;
    private long messageTime;

    public ChatMessage(String messageText, String messageUser, String linkAvatar) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.linkAvatar = linkAvatar;

        messageTime = new Date().getTime();
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

    public String getLinkAvatar() {
        return linkAvatar;
    }

    public void setLinkAvatar(String linkAvatar) {
        this.linkAvatar = linkAvatar;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
