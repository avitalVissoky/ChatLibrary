# UI Customization Guide

Learn how to customize the Chat SDK's appearance to match your app's design and branding.

## 🎨 ChatStyle Overview

The `ChatStyle` class is your main tool for customizing the chat interface appearance. It controls colors, typography, and visual elements.

## 🌈 Color Customization

### Basic Color Setup

```java
ChatStyle style = new ChatStyle(
    bubbleSelfColor,     // Your message bubbles
    bubbleOtherColor,    // Other users' bubbles  
    textSelfColor,       // Your message text
    textOtherColor,      // Other users' text
    backgroundColor,     // Chat background
    sendButtonColor      // Send button accent
);
ChatConfig.setStyle(style);
```

### Predefined Color Schemes

#### Material Blue Theme
```java
ChatStyle materialBlue = new ChatStyle(
    Color.parseColor("#2196F3"),  // Blue bubbles for self
    Color.parseColor("#F5F5F5"),  // Light gray for others
    Color.WHITE,                  // White text on blue
    Color.parseColor("#212121"),  // Dark gray text
    Color.WHITE,                  // White background
    Color.parseColor("#2196F3")   // Blue send button
);
```

#### Dark Theme
```java
ChatStyle darkTheme = new ChatStyle(
    Color.parseColor("#BB86FC"),  // Purple bubbles for self
    Color.parseColor("#3C3C3C"),  // Dark gray for others
    Color.parseColor("#121212"),  // Dark text on purple
    Color.WHITE,                  // White text on dark
    Color.parseColor("#121212"),  // Dark background
    Color.parseColor("#BB86FC")   // Purple send button
);
```

#### Green Theme (WhatsApp-like)
```java
ChatStyle greenTheme = new ChatStyle(
    Color.parseColor("#DCF8C6"),  // Light green for self
    Color.WHITE,                  // White for others
    Color.parseColor("#303030"),  // Dark text on light green
    Color.parseColor("#303030"),  // Dark text on white
    Color.parseColor("#ECE5DD"),  // Beige background
    Color.parseColor("#25D366")   // WhatsApp green
);
```

#### Gradient-Inspired Theme
```java
ChatStyle gradientTheme = new ChatStyle(
    Color.parseColor("#667eea"),  // Gradient blue for self
    Color.parseColor("#f093fb"),  // Gradient pink for others
    Color.WHITE,                  // White text
    Color.WHITE,                  // White text
    Color.parseColor("#ffecd2"),  // Light gradient background
    Color.parseColor("#667eea")   // Blue send button
);

## 🏗️ Layout Customization

### Custom Message Layouts

While the SDK provides default layout, you can create custom message layouts by extending the existing components:

#### Custom Message Item Layout

Create `item_message_custom.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="8dp">
    
    <!-- User Avatar -->
    <ImageView
        android:id="@+id/userAvatar"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:layout_alignParentStart="true"
        android:layout_marginEnd="8dp"
        android:background="@drawable/circle_background"
        android:src="@drawable/ic_person" />
    
    <!-- Message Container -->
    <LinearLayout
        android:id="@+id/messageContainer"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_toEndOf="@id/userAvatar"
        android:layout_toStartOf="@id/messageTime"
        android:background="@drawable/message_bubble"
        android:orientation="vertical"
        android:padding="12dp">
        
        <!-- Sender Name -->
        <TextView
            android:id="@+id/senderName"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textColor="#666666"
            android:textSize="12sp"
            android:textStyle="bold" />
        
        <!-- Message Text -->
        <TextView
            android:id="@+id/messageText"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="4dp"
            android:textColor="#000000"
            android:textSize="16sp" />
            
    </LinearLayout>
    
    <!-- Timestamp -->
    <TextView
        android:id="@+id/messageTime"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentEnd="true"
        android:layout_centerVertical="true"
        android:textColor="#999999"
        android:textSize="10sp" />
        
</RelativeLayout>
```

### Custom Bubble Shapes

Create custom bubble drawables in `res/drawable/`:

#### Rounded Bubble (`message_bubble_self.xml`)
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#007AFF" />
    <corners
        android:topLeftRadius="18dp"
        android:topRightRadius="18dp"
        android:bottomLeftRadius="18dp"
        android:bottomRightRadius="4dp" />
</shape>
```

#### Gradient Bubble (`message_bubble_gradient.xml`)
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <gradient
