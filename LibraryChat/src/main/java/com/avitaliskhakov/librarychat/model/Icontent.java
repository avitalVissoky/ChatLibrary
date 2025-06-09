package com.avitaliskhakov.librarychat.model;

public interface Icontent {

    public enum contentType{
        TEXT,
        AUDIO,
        IMG,
        VIDEO
    }

    public contentType getContentType();
    public String getContent();
    public String getCreatedAt();
    public void setCreatedAt(String dateTimeUtc);

}
