package com.example.librarychat.config;
public class ChatConfig {
    private static ChatEventsListener eventsListener;

    private static ChatStyle style;

    public static void setStyle(ChatStyle chatStyle) {
        style = chatStyle;
    }

    public static ChatStyle getStyle() {
        return style;
    }
    public static void setChatEventsListener(ChatEventsListener listener) {
        eventsListener = listener;
    }

    public static ChatEventsListener getChatEventsListener() {
        return eventsListener;
    }
}
