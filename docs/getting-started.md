# Getting Started

This guide will help you integrate the Chat SDK into your Android application in just a few steps.

## 📋 Prerequisites

Before you begin, ensure you have:
- Android Studio 4.0 or higher
- Android SDK with API level 28 or higher
- Java 11 or higher
- An active internet connection for real-time features

## 🛠️ Installation

### Step 1: Add JitPack Repository

Add it in your settings.gradle.kts at the end of repositories:

```
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}
```

### Step 2: Add the Dependency

Add the Chat SDK dependency to your app's `build.gradle.kts` file:

```gradle
	dependencies {
	        implementation("com.github.avitalVissoky:ChatLibrary:1.0.2")
	}
```

### Step 3: Sync Your Project

Click "Sync Now" in Android Studio to download the dependencies.

## 🚀 Quick Implementation

### Option 1: Complete UI Solution (Recommended for Beginners)

This approach uses the pre-built chat interface:

#### 1. Configure Chat Style

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Configure chat appearance
        ChatStyle style = new ChatStyle(
            ContextCompat.getColor(this, R.color.bubble_self),      // Your messages
            ContextCompat.getColor(this, R.color.bubble_other),     // Other users
            ContextCompat.getColor(this, R.color.text_self),        // Your text color
            ContextCompat.getColor(this, R.color.text_other),       // Other text color
            ContextCompat.getColor(this, R.color.chat_background),  // Background
            ContextCompat.getColor(this, R.color.accent)            // Send button
        );
        ChatConfig.setStyle(style);
        
        // Set up event listener
        ChatConfig.setChatEventsListener(new ChatEventsListener() {
            @Override
            public void onMessagesSeen(String chatRoomId, String lastMessageTimestamp) {
                // Handle read receipts
                Log.d("Chat", "Messages seen in room: " + chatRoomId);
            }
        });
    }
}
```

#### 2. Create Chat Controller

```java
private ChatController chatController;

private void initChatController() {
    chatController = new ChatController(new ChatController.CallBack_Chat() {
        @Override
        public void success(List<Message> messages) {
            // Messages loaded successfully
        }
        
        @Override
        public void chatRoomCreated(Map<String, String> response) {
            String newRoomId = response.get("roomId");
            openChatRoom(newRoomId);
        }
        
        @Override
        public void error(String error) {
            Log.e("ChatSDK", "Error: " + error);
            Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
        }
        
        // Implement other required methods...
        @Override public void messageSent(Map<String, Message> response) {}
        @Override public void messageDeleted(Map<String, String> response) {}
        @Override public void messageUpdated(Map<String, Message> response) {}
        @Override public void typingStatusUpdated(Map<String, Boolean> typingUsers) {}
        @Override public void userChatRoomsFetched(List<ChatRoomInfo> rooms) {}
        @Override public void participantsFetched(List<String> participants) {}
    });
}
```

#### 3. Launch Chat Interface

```java
private void openChatRoom(String roomId) {
    Intent intent = new Intent(this, ChatRoomActivity.class);
    intent.putExtra("USER_ID", currentUserId);
    intent.putExtra("CHAT_ROOM_ID", roomId);
    startActivity(intent);
}

// Create a new chat room
private void createChatRoom() {
    String title = "My Chat Room";
    chatController.createChatRoom(title, currentUserId);
}
```

### Option 2: Functionality Only (Advanced)

This approach gives you complete control over the UI:

```java
// Initialize controller
ChatController chatController = new ChatController(callback);

// Create room
chatController.createChatRoom("Room Title", "userId");

// Send message
Content content = new Content("Hello World!", Icontent.contentType.TEXT, null);
Message message = new Message(null, chatRoomId, senderId, content);
chatController.sendMessage(message);

// Fetch messages
chatController.fetchMessages(chatRoomId, null, 20);

// Manage participants
List<String> participants = Arrays.asList("user1", "user2");
chatController.addParticipants(roomId, participants);
```

## 🎨 Define Colors

Add these colors to your `res/values/colors.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Chat Colors -->
    <color name="bubble_self">#007AFF</color>
    <color name="bubble_other">#E5E5EA</color>
    <color name="text_self">#FFFFFF</color>
    <color name="text_other">#000000</color>
    <color name="chat_background">#F2F2F7</color>
    <color name="accent">#007AFF</color>
</resources>
```

## 📱 Permissions

Add these permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## ✅ Verify Installation

Create a simple test to verify everything is working:

```java
public void testChatSDK() {
    ChatController controller = new ChatController(new ChatController.CallBack_Chat() {
        @Override
        public void success(List<Message> messages) {
            Log.d("ChatSDK", "SDK initialized successfully!");
        }
        
        @Override
        public void error(String error) {
            Log.e("ChatSDK", "SDK error: " + error);
        }
        
        // Implement other required methods...
    });
    
    // Test API connection
    controller.getUserChatRooms("testUser");
}
```

## 🔗 Next Steps

Now that you have the SDK installed:

1. **[Explore the API Reference](android-library-api.md)** - Learn about all available methods
2. **[Check the Example App](example-application.md)** - See a complete implementation
3. **[Customize the UI](ui-customization.md)** - Make it match your app's design
4. **[API Service Setup](api-service.md)** - Configure the backend service

## 🆘 Troubleshooting

### Common Issues

**Build Error: "Could not resolve dependency"**
- Ensure JitPack repository is added correctly
- Check your internet connection
- Try cleaning and rebuilding the project

**Runtime Error: "Network Security Configuration"**
- Add network security config for HTTP endpoints (if needed)
- Ensure INTERNET permission is added

**Chat UI Not Appearing**
- Verify USER_ID and CHAT_ROOM_ID extras are passed correctly
- Check that ChatStyle is configured before launching ChatRoomActivity

---

[← Back to Documentation Home](index.md) | [Android Library API →](android-library-api.md)
