package com.tds.demo.fragment.IM;

import java.util.List;

import cn.leancloud.im.v2.LCIMClient;
import cn.leancloud.im.v2.LCIMConversation;
import cn.leancloud.im.v2.LCIMMessage;

/**
 * 2022/11/1
 * Describe：消息的事件总线
 */
public class InviteEvent {
    private static InviteEvent inviteEvent = null;
    private LCIMClient client;
    private LCIMConversation conversation;
    private List<String> members;
    private String invitedBy;

    public static final InviteEvent getInstance(LCIMClient client, LCIMConversation conversation, List<String> members, String invitedBy) {
        if (inviteEvent == null) {
            inviteEvent = new InviteEvent();
        }
        inviteEvent.setClient(client);
        inviteEvent.setInvitedBy(invitedBy);
        inviteEvent.setConversation(conversation);
        inviteEvent.setMembers(members);

        return inviteEvent;
    }

    public void setConversation(LCIMConversation conversation) {
        this.conversation = conversation;
    }

    public void setClient(LCIMClient client) {
        this.client = client;
    }

    public void setInvitedBy(String invitedBy) {
        this.invitedBy = invitedBy;
    }

    public static void setInviteEvent(InviteEvent inviteEvent) {
        InviteEvent.inviteEvent = inviteEvent;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public LCIMConversation getConversation() {
        return conversation;
    }

    public String getInvitedBy() {
        return invitedBy;
    }

    public LCIMClient getClient() {
        return client;
    }

    public List<String> getMembers() {
        return members;
    }

    @Override
    public String toString() {
        return "InviteEvent{" +
                "client=" + client +
                ", conversation=" + conversation +
                ", members=" + members +
                ", invitedBy='" + invitedBy + '\'' +
                '}';
    }
}

