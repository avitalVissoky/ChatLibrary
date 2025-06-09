package com.avitaliskhakov.librarychat.config;

import androidx.annotation.ColorInt;

public class ChatStyle {

    @ColorInt public int bubbleSelfColor;
    @ColorInt public int bubbleOtherColor;
    @ColorInt public int textSelfColor;
    @ColorInt public int textOtherColor;
    @ColorInt public int backgroundColor;

    @ColorInt public int sendButtonColor;


    public ChatStyle(
            int bubbleSelfColor,
            int bubbleOtherColor,
            int textSelfColor,
            int textOtherColor,
            int backgroundColor,
            int sendButtonColor
    ) {
        this.bubbleSelfColor = bubbleSelfColor;
        this.bubbleOtherColor = bubbleOtherColor;
        this.textSelfColor = textSelfColor;
        this.textOtherColor = textOtherColor;
        this.backgroundColor = backgroundColor;
        this.sendButtonColor = sendButtonColor;
    }

}
