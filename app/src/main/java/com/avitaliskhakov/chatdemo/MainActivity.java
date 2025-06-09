package com.avitaliskhakov.chatdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.avitaliskhakov.librarychat.config.ChatConfig;
import com.avitaliskhakov.librarychat.api.ChatController;
import com.avitaliskhakov.librarychat.config.ChatEventsListener;
import com.avitaliskhakov.librarychat.ui.ChatRoomActivity;
import com.avitaliskhakov.librarychat.model.ChatRoomInfo;
import com.avitaliskhakov.librarychat.config.ChatStyle;
import com.avitaliskhakov.librarychat.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput;
    private Button loginButton;
    private ListView chatRoomsListView;
    private Button createRoomButton;

    private String currentUserId;
    private ChatController chatController;
    private ArrayAdapter<String> adapter;

    private final List<ChatRoomInfo> chatRooms = new ArrayList<>();
    private final List<String> roomTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = findViewById(R.id.usernameInput);
        loginButton = findViewById(R.id.loginButton);
        chatRoomsListView = findViewById(R.id.chatRoomsListView);
        createRoomButton = findViewById(R.id.createRoomButton);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roomTitles);
        chatRoomsListView.setAdapter(adapter);

        ChatStyle style = new ChatStyle(
                ContextCompat.getColor(this, R.color.bubble_self),
                ContextCompat.getColor(this, R.color.bubble_other),
                ContextCompat.getColor(this, R.color.text_self),
                ContextCompat.getColor(this, R.color.text_other),
                ContextCompat.getColor(this, R.color.chat_background),
                ContextCompat.getColor(this, R.color.bubble_self) // same color as bubble_self for compatability
        );
        ChatConfig.setStyle(style);
        ChatConfig.setChatEventsListener(new ChatEventsListener() {
            @Override
            public void onMessagesSeen(String chatRoomId, String lastMessageTimestamp) {

            }
        });

        loginButton.setOnClickListener(v -> {
            String userId = usernameInput.getText().toString().trim();
            if (!userId.isEmpty()) {
                currentUserId = userId;
                initChatController();
                fetchUserRooms();
            }
        });

        createRoomButton.setOnClickListener(v -> {
            if (currentUserId != null) {
                String title = "Room by " + currentUserId;
                chatController.createChatRoom(title, currentUserId);
            }
        });

        chatRoomsListView.setOnItemClickListener((parent, view, position, id) -> {
            String roomId = chatRooms.get(position).getId();
            openChatRoom(roomId);
        });
    }

    private void initChatController() {
        chatController = new ChatController(new ChatController.CallBack_Chat() {
            @Override public void success(List<Message> messages) {}
            @Override public void messageSent(Map<String, Message> response) {}
            @Override public void messageDeleted(Map<String, String> response) {}
            @Override public void messageUpdated(Map<String, Message> response) {}
            @Override public void typingStatusUpdated(Map<String, Boolean> typingUsers) {}

            @Override
            public void chatRoomCreated(Map<String, String> response) {
                String newRoomId = response.get("roomId");

                List<String> initialParticipants = new ArrayList<>();
                initialParticipants.add(currentUserId);
                initialParticipants.add("demoUser");

                chatController.addParticipants(newRoomId, initialParticipants);
                openChatRoom(newRoomId);
            }

            @Override
            public void userChatRoomsFetched(List<ChatRoomInfo> rooms) {
                runOnUiThread(() -> {
                    chatRooms.clear();
                    chatRooms.addAll(rooms);

                    roomTitles.clear();
                    for (ChatRoomInfo room : rooms) {
                        roomTitles.add(room.getTitle());
                    }

                    adapter.notifyDataSetChanged();
                });
            }

            @Override public void participantsFetched(List<String> participants) {}

            @Override
            public void error(String error) {
                Log.e("MainActivity", "Error SDK: " + error);
            }
        });
    }

    private void fetchUserRooms() {
        if (chatController != null) {
            chatController.getUserChatRooms(currentUserId);
        }
    }

    private void openChatRoom(String roomId) {
        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.putExtra("USER_ID", currentUserId);
        intent.putExtra("CHAT_ROOM_ID", roomId);
        startActivity(intent);
    }
}
