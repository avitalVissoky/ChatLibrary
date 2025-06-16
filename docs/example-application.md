# Example Application Walkthrough

This guide walks through the complete example Android application that demonstrates how to use the Chat SDK. The example app shows both basic and advanced usage patterns.

## 📱 App Overview

The example application includes:
- **Chat room listing**
- **Room creation**
- **Real-time messaging interface**
- **Message editing and deletion**
- **Typing indicators**
- **Custom styling**

## 🏗️ App Structure

```
app/
├── MainActivity.java          # Main screen with room list
├── res/
│   ├── layout/
│   │   └── activity_chat_demo_with_state.xml
│   ├── values/
│   │   ├── colors.xml
│   │   └── strings.xml
└── AndroidManifest.xml
```

## 📋 MainActivity.java Breakdown

### 1. Setup and Initialization

```java
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
```

**Key Components:**
- **Username input** for user identification
- **Room list** showing available chat rooms
- **Create room button** for new room creation
- **Chat controller** for API communication

### 2. Style Configuration

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    // Configure chat appearance
    ChatStyle style = new ChatStyle(
        ContextCompat.getColor(this, R.color.bubble_self),      // #007AFF
        ContextCompat.getColor(this, R.color.bubble_other),     // #E5E5EA  
        ContextCompat.getColor(this, R.color.text_self),        // #FFFFFF
        ContextCompat.getColor(this, R.color.text_other),       // #000000
        ContextCompat.getColor(this, R.color.chat_background),  // #F2F2F7
        ContextCompat.getColor(this, R.color.accent)            // #007AFF
    );
    ChatConfig.setStyle(style);
```

**Style Explanation:**
- **bubble_self**: Blue color for user's own messages
- **bubble_other**: Light gray for other users' messages
- **text_self**: White text on blue background
- **text_other**: Black text on gray background
- **chat_background**: Light gray chat background
- **accent**: Blue accent color for UI elements

### 3. Event Listener Setup

```java
// Set up global chat event listener
ChatConfig.setChatEventsListener(new ChatEventsListener() {
    @Override
    public void onMessagesSeen(String chatRoomId, String lastMessageTimestamp) {
        // Handle read receipts
        Log.d("MainActivity", "Messages seen in room: " + chatRoomId);
        // You could update read status indicators here
    }
});
```

### 4. User Authentication Flow

```java
loginButton.setOnClickListener(v -> {
    String userId = usernameInput.getText().toString().trim();
    if (!userId.isEmpty()) {
        currentUserId = userId;
        initChatController();
        fetchUserRooms();
    } else {
        Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
    }
});
```

**Authentication Notes:**
- Currently uses simple username input
- In production, integrate with your authentication system
- Store user session data securely

### 5. Chat Controller Initialization

```java
private void initChatController() {
    chatController = new ChatController(new ChatController.CallBack_Chat() {
        @Override
        public void success(List<Message> messages) {
            // Handle message loading
        }
        
        @Override
        public void chatRoomCreated(Map<String, String> response) {
            String newRoomId = response.get("roomId");
            
            // Add initial participants
            List<String> initialParticipants = new ArrayList<>();
            initialParticipants.add(currentUserId);
            initialParticipants.add("demoUser"); // Add demo user for testing
            
            chatController.addParticipants(newRoomId, initialParticipants);
            openChatRoom(newRoomId);
        }
        
        @Override
        public void userChatRoomsFetched(List<ChatRoomInfo> rooms) {
            runOnUiThread(() -> {
                // Update UI with room list
                chatRooms.clear();
                chatRooms.addAll(rooms);
                
                roomTitles.clear();
                for (ChatRoomInfo room : rooms) {
                    roomTitles.add(room.getTitle());
                }
                
                adapter.notifyDataSetChanged();
            });
        }
        
        @Override
        public void error(String error) {
            Log.e("MainActivity", "Chat SDK Error: " + error);
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, 
                    "Error: " + error, Toast.LENGTH_LONG).show();
            });
        }
        
        // Other required callback methods
        @Override public void messageSent(Map<String, Message> response) {}
        @Override public void messageDeleted(Map<String, String> response) {}
        @Override public void messageUpdated(Map<String, Message> response) {}
        @Override public void typingStatusUpdated(Map<String, Boolean> typingUsers) {}
        @Override public void participantsFetched(List<String> participants) {}
    });
}
```

### 6. Room Management

```java
// Create new chat room
createRoomButton.setOnClickListener(v -> {
    if (currentUserId != null) {
        String title = "Room by " + currentUserId;
        chatController.createChatRoom(title, currentUserId);
    } else {
        Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
    }
});

// Join existing room
chatRoomsListView.setOnItemClickListener((parent, view, position, id) -> {
    if (position < chatRooms.size()) {
        String roomId = chatRooms.get(position).getId();
        openChatRoom(roomId);
    }
});

// Navigate to chat interface
private void openChatRoom(String roomId) {
    Intent intent = new Intent(this, ChatRoomActivity.class);
    intent.putExtra("USER_ID", currentUserId);
    intent.putExtra("CHAT_ROOM_ID", roomId);
    startActivity(intent);
}
```

## 🎨 UI Layout Examples

### activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">
    
    <!-- Login Section -->
    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Enter Username">
        
        <EditText
            android:id="@+id/usernameInput"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="text" />
            
    </com.google.android.material.textfield.TextInputLayout>
    
    <Button
        android:id="@+id/loginButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Login"
        android:layout_marginTop="8dp" />
    
    <!-- Room Management -->
    <Button
        android:id="@+id/createRoomButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Create New Room"
        android:layout_marginTop="16dp" />
    
    <!-- Room List -->
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Available Rooms"
        android:textSize="18sp"
        android:textStyle="bold"
        android:layout_marginTop="16dp" />
    
    <ListView
        android:id="@+id/chatRoomsListView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:layout_marginTop="8dp" />
        
</LinearLayout>
```

### colors.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Chat SDK Colors -->
    <color name="bubble_self">#007AFF</color>
    <color name="bubble_other">#E5E5EA</color>
    <color name="text_self">#FFFFFF</color>
    <color name="text_other">#000000</color>
    <color name="chat_background">#F2F2F7</color>
    <color name="accent">#007AFF</color>
    
    <!-- App Theme Colors -->
    <color name="primary">#007AFF</color>
    <color name="primary_dark">#0056CC</color>
    <color name="background">#F2F2F7</color>
    <color name="surface">#FFFFFF</color>
    <color name="error">#FF3B30</color>
</resources>
```

## 📱 Chat Interface Features

The example demonstrates the full chat interface provided by `ChatRoomActivity`:

### Message Display
- **Self messages**: Right-aligned, blue bubbles
- **Other messages**: Left-aligned, gray bubbles
- **Timestamps**: Formatted as "HH:mm, dd/MM/yyyy"
- **Edit indicators**: Shows "(edited at timestamp)" for modified messages

### User Interactions
- **Long press**: Edit your own messages
- **Double tap**: Delete your own messages
- **Typing**: Real-time typing indicators
- **Scroll**: Automatic pagination when scrolling up

### State Management
- **Loading state**: Spinner while fetching messages
- **Empty state**: "No messages yet" when room is empty
- **Error state**: Retry button for failed requests
- **Content state**: Normal chat interface

## 🔧 Customization Examples

### Custom Message Styling

```java
// In your Application class or main activity
ChatStyle customStyle = new ChatStyle(
    Color.parseColor("#4CAF50"),  // Green for self messages
    Color.parseColor("#FFF3E0"),  // Light orange for others
    Color.WHITE,                  // White text on green
    Color.parseColor("#5D4037"),  // Brown text on orange
    Color.parseColor("#FAFAFA"),  // Very light gray background
    Color.parseColor("#4CAF50")   // Green send button
);
ChatConfig.setStyle(customStyle);
```


### Error Handling

```java
@Override
public void error(String error) {
    runOnUiThread(() -> {
        if (error.contains("network") || error.contains("timeout")) {
            // Network error - show retry option
            showNetworkErrorDialog();
        } else if (error.contains("unauthorized")) {
            // Authentication error - redirect to login
            redirectToLogin();
        } else {
            // Generic error
            Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
        }
    });
}

```

## 🚀 Advanced Features

### Room Management

```java
// Get room participants
private void showRoomParticipants(String roomId) {
    chatController.getParticipantsInRoom(roomId);
}

// Add new participants
private void addParticipantsToRoom(String roomId, List<String> userIds) {
    chatController.addParticipants(roomId, userIds);
}

// Handle participants callback
@Override
public void participantsFetched(List<String> participants) {
    runOnUiThread(() -> {
        // Show participants list
        showParticipantsDialog(participants);
    });
}
```



## 📊 Performance Optimization

### Efficient Loading

```java
// Implement smart pagination
private static final int INITIAL_LOAD = 20;
private static final int PAGE_SIZE = 10;

private void loadMessages(boolean isInitialLoad) {
    int limit = isInitialLoad ? INITIAL_LOAD : PAGE_SIZE;
    chatController.fetchMessages(chatRoomId, lastTimestamp, limit);
}

// Cache frequently accessed data
private final Map<String, ChatRoomInfo> roomCache = new HashMap<>();

private void cacheRoom(ChatRoomInfo room) {
    roomCache.put(room.getId(), room);
}
```




## 🎯 Next Steps

After reviewing this example:

1. **[Customize the UI](ui-customization.md)** to match your app's design
2. **[Review API Reference](android-library-api.md)** for advanced features
3. **[Set up the backend](api-service.md)** for production use
4. **[Configure advanced features](advanced-configuration.md)** like authentication

## 📁 Download Example

The complete example application is available in the GitHub repository

---

[← API Service](api-service.md) | [UI Customization →](ui-customization.md)
