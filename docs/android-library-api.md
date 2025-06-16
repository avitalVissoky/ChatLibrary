# Android Library API Reference

Complete API documentation for the Chat SDK Android library.

## 📱 Core Classes

### ChatController

The main controller class for managing chat functionality.

#### Constructor

```java
public ChatController(CallBack_Chat callBackChat)
```

**Parameters:**
- `callBackChat` - Callback interface to handle API responses

#### Methods

##### Chat Room Management

```java
// Create a new chat room
public void createChatRoom(String title, String creatorId)
```
- **title** - The display name for the chat room
- **creatorId** - User ID of the room creator

```java
// Get all chat rooms for a user
public void getUserChatRooms(String userId)
```
- **userId** - The user ID to fetch rooms for

```java
// Add participants to a chat room
public void addParticipants(String roomId, List<String> userIds)
```
- **roomId** - The chat room ID
- **userIds** - List of user IDs to add

```java
// Get participants in a room
public void getParticipantsInRoom(String roomId)
```
- **roomId** - The chat room ID

##### Message Management

```java
// Fetch messages with pagination
public void fetchMessages(String chatRoomId, String lastCreatedAt, int limit)
```
- **chatRoomId** - The chat room ID
- **lastCreatedAt** - Timestamp for pagination (null for first page)
- **limit** - Maximum number of messages to fetch

```java
// Send a new message
public void sendMessage(Message message)
```
- **message** - Message object to send

```java
// Update an existing message
public void updateMessage(String msgId, Content content)
```
- **msgId** - ID of the message to update
- **content** - New content for the message

```java
// Delete a message
public void deleteMessage(String msgId, String chatRoomId)
```
- **msgId** - ID of the message to delete
- **chatRoomId** - The chat room ID

##### Typing Status

```java
// Set typing status for a user
public void setTypingStatus(String chatRoomId, String userId, boolean isTyping)
```
- **chatRoomId** - The chat room ID
- **userId** - The user who is typing
- **isTyping** - Whether the user is currently typing

```java
// Fetch current typing status
public void fetchTypingStatus(String chatRoomId)
```
- **chatRoomId** - The chat room ID

#### CallBack_Chat Interface

```java
public interface CallBack_Chat {
    void success(List<Message> messages);
    void messageSent(Map<String, Message> response);
    void messageDeleted(Map<String, String> response);
    void messageUpdated(Map<String, Message> response);
    void typingStatusUpdated(Map<String, Boolean> typingUsers);
    void chatRoomCreated(Map<String, String> response);
    void userChatRoomsFetched(List<ChatRoomInfo> chatRooms);
    void participantsFetched(List<String> participants);
    void error(String error);
}
```

### ChatConfig

Static configuration class for global chat settings.

#### Methods

```java
// Set global chat style
public static void setStyle(ChatStyle chatStyle)
```

```java
// Get current chat style
public static ChatStyle getStyle()
```

```java
// Set event listener for chat events
public static void setChatEventsListener(ChatEventsListener listener)
```

```java
// Get current event listener
public static ChatEventsListener getChatEventsListener()
```

### ChatStyle

Customization class for chat appearance.

#### Constructor

```java
public ChatStyle(
    int bubbleSelfColor,
    int bubbleOtherColor, 
    int textSelfColor,
    int textOtherColor,
    int backgroundColor,
    int sendButtonColor
)
```

**Parameters:**
- `bubbleSelfColor` - Color for your message bubbles
- `bubbleOtherColor` - Color for other users' message bubbles
- `textSelfColor` - Text color for your messages
- `textOtherColor` - Text color for other users' messages
- `backgroundColor` - Chat background color
- `sendButtonColor` - Send button color

#### Example Usage

```java
ChatStyle style = new ChatStyle(
    Color.parseColor("#007AFF"),  // Blue bubbles for self
    Color.parseColor("#E5E5EA"),  // Gray bubbles for others
    Color.WHITE,                  // White text for self
    Color.BLACK,                  // Black text for others
    Color.parseColor("#F2F2F7"),  // Light gray background
    Color.parseColor("#007AFF")   // Blue send button
);
ChatConfig.setStyle(style);
```

## 📋 Data Models

### Message

```java
public class Message {
    private String id;
    private String chatRoomId;
    private String senderId;
    private Content content;
    private boolean edited;
    private String createdAt;
    
    // Constructor
    public Message(String id, String chatRoomId, String senderId, Content content)
    
    // Getters and Setters
    public String getId()
    public void setId(String id)
    public String getChatRoomId()
    public void setChatRoomId(String chatRoomId)
    public String getSenderId()
    public void setSenderId(String senderId)
    public Content getContent()
    public void setContent(Content content)
    public boolean isEdited()
    public void setEdited(boolean edited)
    public String getCreatedAt()
    public void setCreatedAt(String createdAt)
}
```

### Content

```java
public class Content implements Icontent {
    private String content;
    private contentType contentType;
    private String createdAt;
    
    // Constructor
    public Content(String content, contentType contentType, String createdAt)
    
    // Methods
    public String getContent()
    public void setContent(String content)
    public contentType getContentType()
    public void setContentType(contentType contentType)
    public String getCreatedAt()
    public void setCreatedAt(String dateTimeUtc)
}
```

### contentType Enum

```java
public enum contentType {
    TEXT,    // Text messages
    AUDIO,   // Audio messages (future)
    IMG,     // Image messages (future)
    VIDEO    // Video messages (future)
}
```

### ChatRoomInfo

```java
public class ChatRoomInfo {
    private String id;
    private String title;
    private String creator;
    
    // Constructor
    public ChatRoomInfo(String id, String title)
    
    // Getters and Setters
    public String getId()
    public void setId(String id)
    public String getTitle()
    public void setTitle(String title)
    public String getCreator()
    public void setCreator(String creator)
}
```

## 🎯 Interfaces

### ChatEventsListener

```java
public interface ChatEventsListener {
    void onMessagesSeen(String chatRoomId, String lastMessageTimestamp);
}
```

### UserImageProvider

```java
public interface UserImageProvider {
    String getImageUrlForUser(String userId);
}
```

## 🖼️ UI Components

### ChatRoomActivity

Pre-built activity for displaying chat interface.

#### Required Extras

```java
Intent intent = new Intent(context, ChatRoomActivity.class);
intent.putExtra("USER_ID", currentUserId);      // Required
intent.putExtra("CHAT_ROOM_ID", chatRoomId);    // Required
startActivity(intent);
```

#### Features

- **Message List** - Displays all messages with sender bubbles
- **Input Field** - Text input with send button
- **Typing Indicators** - Shows when other users are typing
- **Message Actions** - Long press to edit, double tap to delete
- **Pagination** - Automatic loading of message history
- **Multi-State Views** - Loading, empty, error, and content states

### MessageAdapter

RecyclerView adapter for displaying messages.

#### Constructor

```java
public MessageAdapter(List<Message> messages, String currentUserId, MessageClickListener listener)
```

#### MessageClickListener Interface

```java
public interface MessageClickListener {
    void onMessageLongClicked(Message message);    // For editing
    void onMessageDoubleClicked(Message message);  // For deleting
}
```

## 🛠️ Utility Classes

### ChatViewUtils

```java
public class ChatViewUtils {
    // Apply color tint to chat bubble background
    public static void applyChatBubbleColor(View view, int color)
}
```

## 📱 Complete Implementation Example

```java
public class ChatActivity extends AppCompatActivity {
    private ChatController chatController;
    private String currentUserId = "user123";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Configure style
        setupChatStyle();
        
        // Initialize controller
        initChatController();
        
        // Create or join room
        createChatRoom("General Chat");
    }
    
    private void setupChatStyle() {
        ChatStyle style = new ChatStyle(
            ContextCompat.getColor(this, R.color.primary),
            ContextCompat.getColor(this, R.color.gray_light),
            Color.WHITE,
            Color.BLACK,
            ContextCompat.getColor(this, R.color.background),
            ContextCompat.getColor(this, R.color.accent)
        );
        ChatConfig.setStyle(style);
        
        ChatConfig.setChatEventsListener(new ChatEventsListener() {
            @Override
            public void onMessagesSeen(String chatRoomId, String lastMessageTimestamp) {
                // Handle read receipts
                updateReadStatus(chatRoomId, lastMessageTimestamp);
            }
        });
    }
    
    private void initChatController() {
        chatController = new ChatController(new ChatController.CallBack_Chat() {
            @Override
            public void success(List<Message> messages) {
                // Messages loaded
                Log.d("Chat", "Loaded " + messages.size() + " messages");
            }
            
            @Override
            public void chatRoomCreated(Map<String, String> response) {
                String roomId = response.get("roomId");
                openChatRoom(roomId);
            }
            
            @Override
            public void messageSent(Map<String, Message> response) {
                Message sentMessage = response.get("message");
                Log.d("Chat", "Message sent: " + sentMessage.getId());
            }
            
            @Override
            public void error(String error) {
                Log.e("Chat", "Error: " + error);
                showErrorDialog(error);
            }
            
            // Implement other required methods...
        });
    }
    
    private void createChatRoom(String title) {
        chatController.createChatRoom(title, currentUserId);
    }
    
    private void openChatRoom(String roomId) {
        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.putExtra("USER_ID", currentUserId);
        intent.putExtra("CHAT_ROOM_ID", roomId);
        startActivity(intent);
    }
}
```

## 🔗 Related Documentation

- [Getting Started Guide](getting-started.md)
- [UI Customization](ui-customization.md)
- [Example Application](example-application.md)
- [API Service Documentation](api-service.md)

---

[← Getting Started](getting-started.md) | [API Service →](api-service.md)
