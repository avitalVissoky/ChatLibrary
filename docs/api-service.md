# API Service Documentation

This document describes the REST API endpoints used by the Chat SDK. The API service is built with **Spring Boot** and uses **Firebase Realtime Database** for data storage.

## 🌐 Base URL

```
Production: https://straightforward-freddy-avital-bcd688c4.koyeb.app
```

## 🏗️ Architecture Overview

The API service consists of:
- **Spring Boot REST Controllers** for HTTP endpoints
- **Firebase Realtime Database** for real-time data storage
- **Asynchronous processing** using CompletableFuture
- **Jakarta Bean Validation** for request validation

### Technology Stack
- **Spring Boot 3.x** - Web framework
- **Firebase Admin SDK** - Database integration
- **Jakarta Validation** - Input validation

## 🔐 Authentication

Currently, the API uses user ID-based authentication. All requests require a valid `userId` parameter.

**Firebase Configuration:**
- Database URL: `https://chatsdk-5b40b-default-rtdb.firebaseio.com`

## 📨 Message Endpoints

### Get Messages

Fetch messages from a chat room with pagination support.

```
GET /messages/getMessages
```

**Controller:** `ChatController.getMessages()`

**Query Parameters:**
- `chatRoomId` (string, required) - The chat room ID
- `lastCreatedAt` (string, optional) - Timestamp for pagination (ISO 8601 format)
- `limit` (integer, required, min=1) - Maximum number of messages to return

**Example Request:**
```bash
curl -X GET "https://api.example.com/messages/getMessages?chatRoomId=room123&limit=20" \
  -H "Accept: application/json"
```

**Response:**
```json
[
  {
    "id": "msg123",
    "chatRoomId": "room123",
    "senderId": "user456",
    "content": {
      "content": "Hello, world!",
      "contentType": "TEXT",
      "createdAt": "2024-01-15T10:30:00Z"
    },
    "edited": false,
    "createdAt": "2024-01-15T10:30:00Z"
  }
]
```

**Firebase Data Structure:**
```
ChatRooms/
  {roomId}/
    Messages/
      {messageId}: "createdAt_timestamp"

Messages/
  {messageId}: {MessageBoundary object}
```

**Status Codes:**
- `200 OK` - Messages retrieved successfully
- `400 Bad Request` - Invalid parameters or database error

### Send Message

Send a new message to a chat room.

```
POST /messages/send
```

**Controller:** `ChatController.sendMessage()`

**Headers:**
- `Content-Type: application/json`
- `Accept: application/json`

**Request Body:**
```json
{
  "chatRoomId": "room123",
  "senderId": "user456",
  "content": {
    "content": "Hello, world!",
    "contentType": "TEXT"
  }
}
```

**Validation Rules:**
- `chatRoomId`: Required, not blank
- `senderId`: Required, not blank  
- `content`: Required object with valid ContentBoundary
- `content.content`: Required, not blank
- `content.contentType`: Required enum (TEXT, AUDIO, IMG, VIDEO)

**Response:**
```json
{
  "message": {
    "id": "msg789",
    "chatRoomId": "room123",
    "senderId": "user456",
    "content": {
      "content": "Hello, world!",
      "contentType": "TEXT",
      "createdAt": "2024-01-15T10:35:00Z"
    },
    "edited": false,
    "createdAt": "2024-01-15T10:35:00Z"
  }
}
```

**Process Flow:**
1. Creates message instance in `Messages/` collection
2. Adds message reference to `ChatRooms/{roomId}/Messages/`
3. Auto-generates timestamps using `Instant.now().toString()`

### Update Message

Update an existing message content.

```
POST /messages/update
```

**Controller:** `ChatController.updateMessage()`

**Query Parameters:**
- `msgId` (string, required) - The message ID to update

**Request Body:**
```json
{
  "content": "Updated message content",
  "contentType": "TEXT"
}
```

**Response:**
```json
{
  "Message": {
    "id": "msg789",
    "chatRoomId": "room123",
    "senderId": "user456",
    "content": {
      "content": "Updated message content",
      "contentType": "TEXT",
      "createdAt": "2024-01-15T10:40:00Z"
    },
    "edited": true,
    "createdAt": "2024-01-15T10:35:00Z"
  }
}
```

**Important Notes:**
- Sets `edited` flag to `true`
- Updates content's `createdAt` to edit timestamp
- Preserves original message `createdAt`

### Delete Message

Delete a message from a chat room.

```
DELETE /messages/delete
```

**Controller:** `ChatController.deleteMessage()`

**Query Parameters:**
- `msgId` (string, required) - The message ID to delete
- `chatroomId` (string, required) - The chat room ID

**Response:**
```json
{
  "msgId": "msg789"
}
```

**Process Flow:**
1. Removes message from `Messages/` collection
2. Removes message reference from `ChatRooms/{roomId}/Messages/`
3. Both operations must succeed for successful deletion

## ⌨️ Typing Status Endpoints

### Set Typing Status

Update a user's typing status in a chat room.

```
POST /messages/typing/set
```

**Controller:** `ChatController.setTypingStatus()`

**Query Parameters:**
- `chatRoomId` (string, required) - The chat room ID
- `userId` (string, required) - The user ID
- `isTyping` (boolean, required) - Whether the user is typing

**Firebase Structure:**
```
ChatRooms/
  {roomId}/
    TypingStatus/
      {userId}: true/false
```

**Response:**
```
204 No Content (success)
400 Bad Request (error)
```

### Get Typing Status

Get current typing status for all users in a chat room.

```
GET /messages/typing/get
```

**Controller:** `ChatController.getTypingStatus()`

**Query Parameters:**
- `chatRoomId` (string, required) - The chat room ID

**Response:**
```json
{
  "user456": true,
  "user789": false,
  "user321": true
}
```

## 🏠 Chat Room Endpoints

### Create Chat Room

Create a new chat room.

```
POST /chatrooms/create
```

**Controller:** `ChatRoomController.createChatRoom()`

**Query Parameters:**
- `title` (string, required) - The chat room title
- `creatorId` (string, required) - The user ID of the creator

**Response:**
```json
{
  "roomId": "room123",
  "title": "General Chat"
}
```

**Firebase Structure Created:**
```
ChatRooms/
  {roomId}/
    createdAt: timestamp
    creatorId: "userId"
    title: "Room Title"
    Participants/
      {creatorId}: true
```

### Get User Chat Rooms

Get all chat rooms for a specific user.

```
GET /chatrooms/userRooms
```

**Controller:** `ChatRoomController.getUserChatRooms()`

**Query Parameters:**
- `userId` (string, required) - The user ID

**Response:**
```json
[
  {
    "id": "room123",
    "title": "General Chat",
    "creator": "user456"
  },
  {
    "id": "room456", 
    "title": "Project Discussion",
    "creator": "user789"
  }
]
```

**Implementation Notes:**
- Scans all chat rooms for user participation
- Returns `ChatRoomInfo` objects with ID and title
- Handles missing titles with "Unnamed Room" fallback

### Add Participants

Add participants to an existing chat room.

```
POST /chatrooms/addParticipants
```

**Controller:** `ChatRoomController.addParticipants()`

**Query Parameters:**
- `roomId` (string, required) - The chat room ID

**Request Body:**
```json
["user789", "user321", "user654"]
```

**Response:**
```json
{
  "status": "participants added"
}
```

**Firebase Update:**
```
ChatRooms/{roomId}/Participants/
  user789: true
  user321: true
  user654: true
```

### Get Room Participants

Get all participants in a chat room.

```
GET /chatrooms/participants
```

**Controller:** `ChatRoomController.getParticipantsInRoom()`

**Query Parameters:**
- `roomId` (string, required) - The chat room ID

**Response:**
```json
["user456", "user789", "user321", "user654"]
```

## 📊 Response Formats

### Asynchronous Processing

All endpoints use **CompletableFuture** for asynchronous processing:

```java
@PostMapping(path = "/send")
public CompletableFuture<ResponseEntity<Map<String,MessageBoundary>>> sendMessage(@Valid @RequestBody MessageBoundary msg) {
    CompletableFuture<ResponseEntity<Map<String,MessageBoundary>>> future = new CompletableFuture<>();
    // Async processing...
    return future;
}
```

### Content Types

All API endpoints return JSON data:
```
Content-Type: application/json
```

### Validation

The API uses **Jakarta Bean Validation** with custom annotations:

```java
public class MessageBoundary {
    @NotBlank(message = "chatRoomId is required")
    private String chatRoomId;
    
    @NotBlank(message = "senderId is required") 
    private String senderId;
    
    @NotNull(message = "content is required")
    @Valid
    private ContentBoundary content;
}
```

### Error Responses

When validation fails or database errors occur:

```json
{
  "error": "chatRoomId is required",
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400
}
```

For database operations, errors return:
```json
{
  "error": null  // Generic error indicator
}
```

### Common HTTP Status Codes

- `200 OK` - Request successful
- `204 No Content` - Request successful, no content (typing status)
- `400 Bad Request` - Validation error or database operation failed
- `500 Internal Server Error` - Server error

## 🔥 Firebase Database Structure

### Collections Overview

```
ChatRooms/
  {roomId}/
    createdAt: timestamp
    creatorId: "userId"  
    title: "Room Title"
    Participants/
      {userId}: true
      {userId}: true
    Messages/
      {messageId}: "createdAt_timestamp"
    TypingStatus/
      {userId}: true/false

Messages/
  {messageId}/
    id: "messageId"
    chatRoomId: "roomId"
    senderId: "userId"
    content: {
      content: "message text"
      contentType: "TEXT"
      createdAt: "timestamp"
    }
    edited: false
    createdAt: "timestamp"
```

### Database Operations

**Message Pagination:**
```java
// Get message IDs with pagination
Query query = chatroomMsgRef.orderByValue().limitToLast(limit);
if (lastCreatedAt != null) {
    query = chatroomMsgRef.orderByValue().endAt(lastCreatedAt).limitToLast(limit + 1);
}
```

**Message Storage Pattern:**
1. **Create message instance** in `Messages/` collection
2. **Add reference** in `ChatRooms/{roomId}/Messages/`
3. **Use timestamp** as value for ordering

**Participant Management:**
```java
// Add participants
Map<String, Object> updates = new HashMap<>();
for (String userId : userIds) {
    updates.put(userId, true);
}
participantsRef.updateChildren(updates);
```

## 🚀 Development Setup

### Prerequisites

- **Java 17+** (Jakarta EE)
- **Spring Boot 3.x**
- **Firebase project** with Realtime Database
- **Firebase Admin SDK** credentials

### Environment Configuration

**Required Environment Variables:**
```bash
CHATSDK_CRED='{firebase-service-account-json}'
```

**Firebase Credentials Setup:**
```java
@PostConstruct
public void initializeFirebase() {
    String json = System.getenv("CHATSDK_CRED");
    if (json != null) {
        serviceAccountStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
    } else {
        // Fallback to local file: config/chatsdk-cred.json
    }
}
```

**Local Development:**
1. Create `config/chatsdk-cred.json` with Firebase service account key
2. Start with: `mvn spring-boot:run`
3. API available at: `http://localhost:8088`

### Dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <dependency>
        <groupId>com.google.firebase</groupId>
        <artifactId>firebase-admin</artifactId>
    </dependency>
</dependencies>
```

## 🔄 Real-time Features

### Current Implementation

The API uses **Firebase Realtime Database** for data persistence, but real-time features are currently implemented through **polling** on the client side:

```java
// Android SDK polling example
Handler typingStatusHandler = new Handler();
Runnable typingStatusRunnable = new Runnable() {
    @Override
    public void run() {
        chatController.fetchTypingStatus(chatRoomId);
        typingStatusHandler.postDelayed(this, 700); // Poll every 700ms
    }
};
```


## 📚 Model Classes

### MessageBoundary
```java
public class MessageBoundary {
    private String id;
    @NotBlank(message = "chatRoomId is required")
    private String chatRoomId;
    @NotBlank(message = "senderId is required") 
    private String senderId;
    @NotNull(message = "content is required")
    @Valid
    private ContentBoundary content;
    private boolean isEdited;
    private String createdAt;
}
```

### ContentBoundary
```java
public class ContentBoundary implements IContent {
    @NotBlank(message = "content must not be blank")
    private String content;
    @NotNull(message = "contentType is required")
    private IContent.contentType contentType;
    private String createdAt;
}
```

### ChatRoomInfo
```java
public class ChatRoomInfo {
    private String id;
    private String title; 
    private String creator;
}
```

## 📖 Related Documentation

- [Android Library API](android-library-api.md) - Client-side SDK
- [Getting Started](getting-started.md) - Installation guide
- [Example Application](example-application.md) - Complete implementation

---

[← Android Library API](android-library-api.md) | [Example Application →](example-application.md)


## 📊 Monitoring & Analytics


## 🚀 Production Deployment

### Environment Setup

**Required Environment Variables:**
```bash
# Firebase credentials (JSON string)
CHATSDK_CRED='{"type":"service_account","project_id":"...","private_key":"..."}'

# Server configuration
PORT=8088
SPRING_PROFILES_ACTIVE=production

# Optional: Database configuration
FIREBASE_DATABASE_URL=https://chatsdk-5b40b-default-rtdb.firebaseio.com
```

## 🔗 SDK Integration

The Android SDK automatically handles all API communication:

```java
// SDK Configuration
private static final String BASE_URL = "https://straightforward-freddy-avital-bcd688c4.koyeb.app";

// Retrofit setup with timeouts
OkHttpClient client = new OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build();

// API calls
chatController.fetchMessages(roomId, null, 20);
// Internally makes: GET /messages/getMessages?chatRoomId=roomId&limit=20
```

## 📚 Additional Resources

### Development Tools

- **Spring Boot DevTools**: Hot reloading during development
- **Firebase Console**: Database management and monitoring
- **Postman**: API testing and documentation

### Firebase Resources

- [Firebase Admin SDK Documentation](https://firebase.google.com/docs/admin)
- [Realtime Database Rules](https://firebase.google.com/docs/database/security)
- [Database Structure Best Practices](https://firebase.google.com/docs/database/web/structure-data)

### Spring Boot Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Jakarta Bean Validation](https://beanvalidation.org/)
- [Spring WebMVC](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html)

### Related Documentation

- [Android Library API](android-library-api.md) - Client-side implementation
- [Example Application](example-application.md) - Complete Android app
- [Getting Started](getting-started.md) - Quick setup guide

### Support & Contributing

- **API Issues**: Create issue on [GitHub](https://github.com/avitaliskhakov/ChatLibrary/issues)
- **Server Source**: Available in the main repository
- **Documentation**: [Full docs](https://avitaliskhakov.github.io/ChatLibrary/)

---

[← Android Library API](android-library-api.md) | [Example Application →](example-application.md)
