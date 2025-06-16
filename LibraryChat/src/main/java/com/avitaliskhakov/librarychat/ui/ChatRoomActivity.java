package com.avitaliskhakov.librarychat.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avitaliskhakov.librarychat.R;
import com.avitaliskhakov.librarychat.api.ChatController;
import com.avitaliskhakov.librarychat.config.ChatConfig;
import com.avitaliskhakov.librarychat.config.ChatEventsListener;
import com.avitaliskhakov.librarychat.config.ChatStyle;
import com.avitaliskhakov.librarychat.model.ChatRoomInfo;
import com.avitaliskhakov.librarychat.model.Content;
import com.avitaliskhakov.librarychat.model.Icontent;
import com.avitaliskhakov.librarychat.model.Message;
import com.avitaliskhakov.multistateviewx.MultiStateView;
import com.avitaliskhakov.multistateviewx.State;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatRoomActivity extends AppCompatActivity implements MessageAdapter.MessageClickListener {

    private EditText inputMessage;
    private RecyclerView recyclerView;
    private TextView typingText;
    private View typingContainer;
    private MaterialButton sendBtn;
    private MultiStateView multiStateView;

    private MessageAdapter messageAdapter;
    private final List<Message> messages = new ArrayList<>();
    private final Set<String> loadedMessageIds = new HashSet<>();
    private ChatController chatController;
    private LinearLayoutManager layoutManager;

    private String chatRoomId;
    private String senderId;
    private boolean isLoading = false;
    private String lastCreatedAt = null;
    private final int PAGE_SIZE = 10;
    private boolean isFirstLoad = true;

    private final Handler typingStatusHandler = new Handler();
    private final int TYPING_STATUS_INTERVAL = 700;
    private final Runnable typingStatusRunnable = new Runnable() {
        @Override
        public void run() {
            chatController.fetchTypingStatus(chatRoomId);
            typingStatusHandler.postDelayed(typingStatusRunnable, TYPING_STATUS_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_demo_with_state);

        initView();
        initChat();
        initListeners();
        setupMultiStateView();

        multiStateView.setState(State.LOADING);

        typingStatusHandler.post(typingStatusRunnable);
        fetchMessages(true);
    }

    private void initView() {
        inputMessage = findViewById(R.id.inputMessage);
        recyclerView = findViewById(R.id.recyclerView);
        typingText = findViewById(R.id.typingIndicator);
        typingContainer = findViewById(R.id.typingIndicatorContainer);
        sendBtn = findViewById(R.id.sendBtn);
        multiStateView = findViewById(R.id.multiStateView);

        ChatStyle style = ChatConfig.getStyle();
        if (style != null) {
            sendBtn.setBackgroundTintList(ColorStateList.valueOf(style.sendButtonColor));
        }
    }

    private void setupMultiStateView() {
        multiStateView.setCustomLayout(State.EMPTY, R.layout.layout_chat_empty_state);
        multiStateView.setCustomLayout(State.ERROR, R.layout.layout_chat_error_state);
        multiStateView.setCustomLayout(State.LOADING, R.layout.layout_chat_loading_state);
        multiStateView.setOnRetryClickListener(v -> {
            multiStateView.setState(State.LOADING);
            fetchMessages(isFirstLoad);
        });
    }

    private void initChat() {
        Intent intent = getIntent();
        senderId = intent.getStringExtra("USER_ID");
        chatRoomId = intent.getStringExtra("CHAT_ROOM_ID");

        if (senderId == null || chatRoomId == null) {
            multiStateView.setState(State.ERROR);
            Toast.makeText(this, "Missing USER_ID or CHAT_ROOM_ID", Toast.LENGTH_LONG).show();
            return;
        }

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(messages, senderId, this);
        recyclerView.setAdapter(messageAdapter);

        chatController = new ChatController(createChatCallback());
    }

    private ChatController.CallBack_Chat createChatCallback() {
        return new ChatController.CallBack_Chat() {
            @Override
            public void success(List<Message> newMessages) {
                handleNewMessages(newMessages);
            }

            @Override
            public void messageSent(Map<String, Message> response) {
                handleMessageSent(response);
            }

            @Override
            public void messageDeleted(Map<String, String> response) {
                handleMessageDeleted(response);
            }

            @Override
            public void messageUpdated(Map<String, Message> response) {
                handleMessageUpdated(response);
            }

            @Override
            public void typingStatusUpdated(Map<String, Boolean> typingUsers) {
                updateTypingIndicator(typingUsers);
            }

            @Override
            public void chatRoomCreated(Map<String, String> response) {}

            @Override
            public void userChatRoomsFetched(List<ChatRoomInfo> chatRooms) {}

            @Override
            public void participantsFetched(List<String> participants) {}

            @Override
            public void error(String error) {
                Log.e("ChatRoomActivity", "ERROR SDK: " + error);
                isLoading = false;
                runOnUiThread(() -> {
                    if (error.contains("timeout") && messages.isEmpty()) {
                        multiStateView.setState(State.EMPTY);
                    } else if (messages.isEmpty() && isFirstLoad) {
                        multiStateView.setState(State.ERROR);
                    } else {
                        Toast.makeText(ChatRoomActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
    }

    private void initListeners() {
        inputMessage.addTextChangedListener(new TextWatcher() {
            private boolean isTyping = false;
            private final Handler typingResetHandler = new Handler();
            private final Runnable typingResetRunnable = () -> {
                isTyping = false;
                chatController.setTypingStatus(chatRoomId, senderId, false);
            };

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isTyping) {
                    isTyping = true;
                    chatController.setTypingStatus(chatRoomId, senderId, true);
                }
                typingResetHandler.removeCallbacks(typingResetRunnable);
                typingResetHandler.postDelayed(typingResetRunnable, 1500);
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        sendBtn.setOnClickListener(v -> {
            String text = inputMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                Content content = new Content(text, Icontent.contentType.TEXT, null);
                Message message = new Message(null, chatRoomId, senderId, content);
                chatController.sendMessage(message);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                if (!isLoading && layoutManager.findFirstVisibleItemPosition() == 0 && dx!=0 && dy!=0) {
                    isLoading=true;
                    Log.d("ChatRoomActivity", "onScrolled dx: "+ dx+" dy: "+dy);
                    fetchMessages(false);
                }
            }
        });
    }

    private void handleNewMessages(List<Message> newMessages) {
        runOnUiThread(() -> {
            if (newMessages.isEmpty() && messages.isEmpty()) {
                // No messages at all - show empty state
                multiStateView.setState(State.EMPTY);
                isLoading = false;
                isFirstLoad = false;
                return;
            }

            if (!newMessages.isEmpty()) {
                List<Message> filtered = new ArrayList<>();
                for (Message m : newMessages) {
                    if (!loadedMessageIds.contains(m.getId())) {
                        filtered.add(m);
                        loadedMessageIds.add(m.getId());
                    }
                }
                if (!filtered.isEmpty()) {
                    filtered.sort((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()));
                    lastCreatedAt = filtered.get(0).getCreatedAt();
                    messages.addAll(0, filtered);
                    messageAdapter.notifyItemRangeInserted(0, filtered.size());
                    layoutManager.scrollToPositionWithOffset(filtered.size(), 0);

                    // Show content state
                    multiStateView.setState(State.CONTENT);
                } else if (messages.isEmpty()) {
                    // No new messages and no existing messages - show empty
                    multiStateView.setState(State.EMPTY);
                }
            } else if (messages.isEmpty()) {
                // No new messages and no existing messages - show empty
                multiStateView.setState(State.EMPTY);
            }

            isLoading = false;
            isFirstLoad = false;
        });
    }

    private void handleMessageSent(Map<String, Message> response) {
        runOnUiThread(() -> {
            inputMessage.setText("");
            Message sentMessage = response.get("message");
            if (sentMessage != null) {
                messages.add(sentMessage);
                loadedMessageIds.add(sentMessage.getId());
                messageAdapter.notifyItemInserted(messages.size() - 1);
                recyclerView.scrollToPosition(messages.size() - 1);

                multiStateView.setState(State.CONTENT);

            }
        });
    }

    private void handleMessageDeleted(Map<String, String> response) {
        String deletedMsgId = response.get("msgId");
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId().equals(deletedMsgId)) {
                messages.remove(i);
                loadedMessageIds.remove(deletedMsgId);
                messageAdapter.notifyItemRemoved(i);

                if (messages.isEmpty()) {
                    runOnUiThread(() -> multiStateView.setState(State.EMPTY));
                }
                break;
            }
        }
    }

    private void handleMessageUpdated(Map<String, Message> response) {
        Message updated = response.get("Message");
        if (updated != null) {
            runOnUiThread(() -> {
                for (int i = 0; i < messages.size(); i++) {
                    if (messages.get(i).getId().equals(updated.getId())) {
                        messages.set(i, updated);
                        messageAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            });
        }
    }

    private void updateTypingIndicator(Map<String, Boolean> typingUsers) {
        runOnUiThread(() -> {
            StringBuilder indicator = new StringBuilder();
            for (Map.Entry<String, Boolean> entry : typingUsers.entrySet()) {
//                if ( entry.getValue()) {
                if (!entry.getKey().equals(senderId) && entry.getValue()) {
                    indicator.append(entry.getKey()).append(" is typing...");
                }
            }
            if (indicator.length() > 0) {
                typingText.setText(indicator.toString());
                typingContainer.setVisibility(View.VISIBLE);
            } else {
                typingContainer.setVisibility(View.GONE);
            }
        });
    }

    private void fetchMessages(boolean scrollToBottom) {
        isLoading = true;
        chatController.fetchMessages(chatRoomId, lastCreatedAt, PAGE_SIZE);
        if (scrollToBottom && messages.size() > 0) {
            recyclerView.scrollToPosition(messages.size() - 1);
        }
    }

    @Override
    public void onMessageLongClicked(Message message) {
        if (!message.getSenderId().equals(senderId)) return;

        EditText editText = new EditText(this);
        editText.setText(message.getContent().getContent());

        new AlertDialog.Builder(this)
                .setTitle("Edit Message")
                .setView(editText)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newText = editText.getText().toString().trim();
                    if (!newText.isEmpty()) {
                        Content updated = new Content(newText, Icontent.contentType.TEXT, null);
                        chatController.updateMessage(message.getId(), updated);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onMessageDoubleClicked(Message message) {
        if (!message.getSenderId().equals(senderId)) return;

        new AlertDialog.Builder(this)
                .setTitle("Delete Message")
                .setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Delete", (dialog, which) ->
                        chatController.deleteMessage(message.getId(), chatRoomId)
                )
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        typingStatusHandler.removeCallbacks(typingStatusRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        notifySeenLastMessage();
    }

    private void notifySeenLastMessage() {
        if (messages.isEmpty()) return;
        Message lastMessage = messages.get(messages.size() - 1);
        ChatEventsListener listener = ChatConfig.getChatEventsListener();
        if (listener != null) {
            listener.onMessagesSeen(chatRoomId, lastMessage.getCreatedAt());
        }
    }
}