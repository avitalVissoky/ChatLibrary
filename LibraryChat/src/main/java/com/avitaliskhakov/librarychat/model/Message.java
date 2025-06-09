package com.avitaliskhakov.librarychat.model;

public class Message {
    private String id;
    private String chatRoomId;
    private String senderId;
    private Content content;
    private boolean edited;	// true only if content has changed
    private String createdAt;	//UTC String- if not edited -> equals to content.created at.


    public Message() {}

    public Message(String id, String chatRoomId,String senderId,Content content) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.content = content;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean isEdited) {
        this.edited = isEdited;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Content getContent() {
        return this.content;
    }

    public void setContent(Content content) {
        this.content = content;
    }


}
