package com.avitaliskhakov.librarychat.config;

public interface ChatEventsListener {
    void onMessagesSeen(String chatRoomId, String lastMessageTimestamp);
}
