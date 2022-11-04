package com.tds.demo.fragment.IM;

import com.tds.demo.fragment.GenuineVerifyFragment;

import cn.leancloud.im.v2.LCIMConversation;
import cn.leancloud.im.v2.LCIMMessage;

/**
 * 2022/11/1
 * Describe：消息的事件总线
 */
public class MessageEvent {

    private LCIMConversation lCIMConversation;
    private LCIMMessage lCIMMessage;

    private static MessageEvent messageEvent = null;


    public static final MessageEvent getInstance( LCIMMessage lCIMMessage,
            LCIMConversation lCIMConversation) {
        if (messageEvent == null) {
            messageEvent = new MessageEvent();
        }
        messageEvent.setlCIMMessage(lCIMMessage);
        messageEvent.setlCIMConversation(lCIMConversation);

        return messageEvent;
    }

    public MessageEvent(){

    }
//    public MessageEvent(LCIMMessage lCIMMessage, LCIMConversation lCIMConversation) {
//        this.lCIMMessage = lCIMMessage;
//        this.lCIMConversation = lCIMConversation;
//    }

    public void setlCIMConversation(LCIMConversation lCIMConversation) {
        this.lCIMConversation = lCIMConversation;
    }

    public void setlCIMMessage(LCIMMessage lCIMMessage) {
        this.lCIMMessage = lCIMMessage;
    }

    public LCIMMessage getlCIMMessage() {
        return lCIMMessage;
    }

    public LCIMConversation getlCIMConversation() {
        return lCIMConversation;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "lCIMConversation=" + lCIMConversation +
                ", lCIMMessage=" + lCIMMessage +
                '}';
    }
}

