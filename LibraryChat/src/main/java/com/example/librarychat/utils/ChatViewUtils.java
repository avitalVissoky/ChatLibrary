package com.example.librarychat.utils;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.graphics.drawable.DrawableCompat;

public class ChatViewUtils {

    public static void applyChatBubbleColor(View view, int color) {
        Drawable background = view.getBackground();
        if (background != null) {
            Drawable mutable = DrawableCompat.wrap(background.mutate());
            DrawableCompat.setTint(mutable, color);
            view.setBackground(mutable);
        }
    }
}
