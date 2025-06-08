package com.example.librarychat.ui;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.librarychat.config.ChatConfig;
import com.example.librarychat.config.ChatStyle;
import com.example.librarychat.utils.ChatViewUtils;
import com.example.librarychat.model.Message;
import com.example.librarychat.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public interface MessageClickListener {
        void onMessageLongClicked(Message message);
        void onMessageDoubleClicked(Message message);
    }

    private final List<Message> messages;
    private final String currentUserId;
    private final MessageClickListener listener;

    public MessageAdapter(List<Message> messages, String currentUserId, MessageClickListener listener) {
        this.messages = messages;
        this.currentUserId = currentUserId;
        this.listener = listener;

    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSenderId().equals(currentUserId) ? 0 : 1;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = (viewType == 0) ? R.layout.item_message_self : R.layout.item_message_other;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView timestampTextView;
        private GestureDetector gestureDetector;
        private final ImageView userImage;
        private LinearLayout bubbleLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.messageText);
            timestampTextView = itemView.findViewById(R.id.messageTimestamp);
            userImage = itemView.findViewById(R.id.userImage);
            bubbleLayout = itemView.findViewById(R.id.bubbleLayout);

        }

        public void bind(Message message) {
            textView.setText(message.getContent().getContent());

            ChatStyle style = ChatConfig.getStyle();
            boolean isMine = message.getSenderId().equals(currentUserId);

            if (style != null) {
                textView.setTextColor(isMine ? style.textSelfColor : style.textOtherColor);
                ChatViewUtils.applyChatBubbleColor(bubbleLayout, isMine ? style.bubbleSelfColor : style.bubbleOtherColor);

            }

            String timeText = formatTimestamp(message.getCreatedAt());

            if (message.isEdited()) {
                timeText += "\n (edited at " + formatTimestamp(message.getContent().getCreatedAt()) + ")";
            }

            timestampTextView.setText(timeText);

            gestureDetector = new GestureDetector(itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public void onLongPress(MotionEvent e) {
                    listener.onMessageLongClicked(message);
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    listener.onMessageDoubleClicked(message);
                    return true;
                }
            });

            itemView.setOnTouchListener((v, event) -> {
                gestureDetector.onTouchEvent(event);
                return true;
            });
        }
        private String formatTimestamp(String timestamp) {
            try {
                java.time.OffsetDateTime odt = java.time.OffsetDateTime.parse(timestamp);
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy");
                return odt.format(formatter);
            } catch (Exception e) {
                return "";
            }
        }
    }
}
