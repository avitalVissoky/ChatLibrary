package com.avitaliskhakov.librarychat.model;

public class ChatRoomInfo {
    private String id;
    private String title;
    private String creator;

    public ChatRoomInfo() {} // חובה לריקון עם Firebase / Retrofit

    public ChatRoomInfo(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getCreator(){
        return creator;
    }

    public void setCreator(String creator){
        this.creator=creator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

