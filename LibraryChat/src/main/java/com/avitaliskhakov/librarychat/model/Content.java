package com.avitaliskhakov.librarychat.model;

public class Content implements Icontent{

    private String content;
    private Icontent.contentType contentType;
    private String createdAt;

    public Content() {
    }

    public Content(String content, Icontent.contentType contentType, String createdAt) {
        this.content = content;
        this.contentType = contentType;
        this.createdAt = createdAt;
    }
    @Override
    public Icontent.contentType getContentType() {
        return contentType;
    }

    public void setContentType(Icontent.contentType contentType) {
        this.contentType = contentType;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public String getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public void setCreatedAt(String dateTimeUtc) {
        this.createdAt = dateTimeUtc;
    }
}
