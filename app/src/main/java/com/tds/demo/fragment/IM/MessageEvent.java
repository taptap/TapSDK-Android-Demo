package com.tds.demo.fragment.IM;

import com.tds.demo.fragment.GenuineVerifyFragment;

import cn.leancloud.im.v2.LCIMConversation;

/**
 * 2022/11/1
 * Describe：消息的事件总线
 */
public class MessageEvent {

    private String message;
    private LCIMConversation lCIMConversation;

    private static MessageEvent messageEvent = null;


    public static final MessageEvent getInstance(String message, LCIMConversation lCIMConversation) {
        if (messageEvent == null) {
            messageEvent = new MessageEvent( message,  lCIMConversation);
        }
        return messageEvent;
    }

    public MessageEvent(String message, LCIMConversation lCIMConversation) {
        this.message = message;
        this.lCIMConversation = lCIMConversation;
    }

    public String getMessage() {
        return message;
    }

    public LCIMConversation getlCIMConversation() {
        return lCIMConversation;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "message='" + message + '\'' +
                ", lCIMConversation=" + lCIMConversation +
                '}';
    }
}

