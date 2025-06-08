package com.example.librarychat.config;

public interface ChatEventsListener {
    void onMessagesSeen(String chatRoomId, String lastMessageTimestamp);
}
