# Chat SDK Documentation

Welcome to the Chat SDK documentation! This comprehensive guide will help you integrate real-time chat functionality into your Android application.

## 📚 Documentation Sections

### [🚀 Getting Started](getting-started.md)
Learn how to install and set up the Chat SDK in your Android project.

### [📱 Android Library API](android-library-api.md)
Complete API reference for the Android Chat SDK library with all methods, classes, and interfaces.

### [🛠️ API Service Documentation](api-service.md)
Backend REST API documentation for the chat service endpoints.

### [💡 Example Application](example-application.md)
Step-by-step walkthrough of the example Android application implementation.

### [🎨 UI Customization](ui-customization.md)
Learn how to customize the chat interface to match your app's design.

## 🚀 Quick Start

```
//Add it in your settings.gradle.kts at the end of repositories:
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}
```

```gradle
// Add to your app's build.gradle.kts
	dependencies {
	        implementation("com.github.avitalVissoky:ChatLibrary:Tag")
	}
```

```java
// Basic usage
ChatController chatController = new ChatController(callback);
chatController.createChatRoom("My Room", "userId");
```

## 📖 What's Included

- **Real-time messaging** with WebSocket support
- **Customizable UI components** with Material Design
- **Message management** (send, edit, delete, typing indicators)
- **Chat room management** (create and manage participants)
- **Flexible implementation** (UI + functionality or functionality-only)

---
