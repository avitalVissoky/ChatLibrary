# Chat SDK for Android

A powerful and customizable Android chat library that provides ready-to-use chat functionality with a flexible UI component.

## Features

• **Customizable Chat View** - Ready-to-use chat interface with appearance customization and full chat functionality  
• **Chat Room Creation** - Easy creation of chat rooms  
• **Flexible Usage** - Use with complete chatroom UI or functionality-only without visual interface

## Core Functionality

- 📱 **Real-time messaging** - Send, receive, edit, and delete messages
- 🎨 **Customizable UI** - Fully customizable chat colors, and styling
- 👥 **Multi-user support** - Create chat rooms with multiple participants
- ⌨️ **Typing indicators** - Real-time typing status updates
- 📄 **Message pagination** - Efficient loading of message history
- 🔄 **Message states** - Support for message editing and deletion
- 📱 **Modern UI** - Material Design components with multi-state views

## Installation

### Step 1: Add JitPack repository

Add JitPack repository to your `settings.gradle.kts` at the end of repositories:

```
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}
```

### Step 2: Add the dependency

Add the Chat SDK library to your app's `build.gradle`:

```gradle
dependencies {
  implementation("com.github.avitalVissoky:ChatLibrary:1.0.2")
}
```

## Quick Start

### 1. Configure Chat Style

```java
ChatStyle style = new ChatStyle(
    ContextCompat.getColor(this, R.color.bubble_self),      // Your message bubble color
    ContextCompat.getColor(this, R.color.bubble_other),     // Other user bubble color
    ContextCompat.getColor(this, R.color.text_self),        // Your message text color
    ContextCompat.getColor(this, R.color.text_other),       // Other user text color
    ContextCompat.getColor(this, R.color.chat_background),  // Chat background color
    ContextCompat.getColor(this, R.color.accent)            // Send button color
);
ChatConfig.setStyle(style);
```

### 2. Set Up Event Listener

```java
ChatConfig.setChatEventsListener(new ChatEventsListener() {
    @Override
    public void onMessagesSeen(String chatRoomId, String lastMessageTimestamp) {
        // Handle message seen events
    }
});
```

### 3. Initialize Chat Controller

```java
ChatController chatController = new ChatController(new ChatController.CallBack_Chat() {
    @Override
    public void success(List<Message> messages) {
        // Handle received messages
    }
    
    @Override
    public void messageSent(Map<String, Message> response) {
        // Handle sent message confirmation
    }
    
    @Override
    public void error(String error) {
        // Handle errors
    }
    
    // Implement other callback methods...
});
```

### 4. Use the Complete UI Solution

Launch the ready-to-use chat interface:

```java
Intent intent = new Intent(this, ChatRoomActivity.class);
intent.putExtra("USER_ID", currentUserId);
intent.putExtra("CHAT_ROOM_ID", roomId);
startActivity(intent);
```

### 5. Or Use Functionality Only

Create and manage chat rooms programmatically:

```java
// Create a chat room
chatController.createChatRoom("Room Title", creatorUserId);

// Send a message
Content content = new Content(messageText, Icontent.contentType.TEXT, null);
Message message = new Message(null, chatRoomId, senderId, content);
chatController.sendMessage(message);

// Fetch messages
chatController.fetchMessages(chatRoomId, lastTimestamp, pageSize);
```

## API Reference

### ChatController Methods

| Method | Description |
|--------|-------------|
| `createChatRoom(title, creatorId)` | Create a new chat room |
| `getUserChatRooms(userId)` | Get all chat rooms for a user |
| `addParticipants(roomId, userIds)` | Add participants to a room |
| `sendMessage(message)` | Send a message |
| `fetchMessages(roomId, lastTimestamp, limit)` | Fetch message history |
| `updateMessage(messageId, content)` | Edit a message |
| `deleteMessage(messageId, roomId)` | Delete a message |
| `setTypingStatus(roomId, userId, isTyping)` | Update typing status |

### Models

#### Message
```java
public class Message {
    private String id;
    private String chatRoomId;
    private String senderId;
    private Content content;
    private boolean edited;
    private String createdAt;
}
```

#### Content
```java
public class Content {
    private String content;
    private contentType contentType; // TEXT, AUDIO, IMG, VIDEO
    private String createdAt;
}
```

#### ChatRoomInfo
```java
public class ChatRoomInfo {
    private String id;
    private String title;
    private String creator;
}
```

## Customization

### Styling Options

The `ChatStyle` class allows you to customize:
- Message bubble colors (self/other)
- Text colors (self/other)
- Background color
- Send button color

### UI Components

The SDK includes pre-built layouts:
- Chat message items (self/other)
- Empty state
- Loading state
- Error state with retry functionality

## Usage Examples

### Complete Implementation Example

See the included `MainActivity.java` for a full implementation example showing:
- Chat room listing
- Room creation
- Navigation to chat interface

### Custom Message Handling

```java
@Override
public void messageSent(Map<String, Message> response) {
    Message sentMessage = response.get("message");
    // Update your UI
    runOnUiThread(() -> {
        // Add message to your list
        // Scroll to bottom
        // Clear input field
    });
}
```

## Requirements

- Android API level 28+
- Java 11+
- Internet connection for real-time features

## 🎥 Video

Check out the Chat SDK in action:

https://github.com/user-attachments/assets/bfcc0e0e-dfa6-4ad5-bd67-551cec685d58



## Dependencies

The SDK automatically includes:
- Retrofit for networking
- Gson for JSON parsing
- Material Design Components
- MultiStateViewX for state management

## License

This project is licensed under the MIT License.
