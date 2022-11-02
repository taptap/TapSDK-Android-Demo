package com.tds.demo.fragment.IM;

import java.io.Serializable;

import cn.leancloud.im.v2.LCIMConversation;

/**
 * 2022/10/31
 * Describe：
 */
public class ChatBean implements Serializable {

    private String nickname;
    private transient LCIMConversation conversation;

    public void setConversation(LCIMConversation conversation) {
        this.conversation = conversation;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LCIMConversation getConversation() {
        return conversation;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public String toString() {
        return "ChatBean{" +
                "nickname='" + nickname + '\'' +
                ", conversation=" + conversation +
                '}';
    }
}
