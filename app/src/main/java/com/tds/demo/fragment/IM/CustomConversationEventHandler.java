package com.tds.demo.fragment.IM;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.leancloud.im.v2.LCIMClient;
import cn.leancloud.im.v2.LCIMConversation;
import cn.leancloud.im.v2.LCIMConversationEventHandler;
import cn.leancloud.im.v2.LCIMMessage;
import cn.leancloud.im.v2.LCIMMessageHandler;
import cn.leancloud.im.v2.messages.LCIMTextMessage;

/**
 * 2022/10/31
 * Describe：
 */
// Java/Android SDK 通过定制自己的对话事件 Handler 处理服务端下发的对话事件通知
public class CustomConversationEventHandler extends LCIMConversationEventHandler {
    private static final String TAG = "CustomConversationEvent";
    @Override
    public void onMemberLeft(LCIMClient client, LCIMConversation conversation, List<String> members, String kickedBy) {
        Log.e(TAG, "onMemberLeft: "+client+"  "+ conversation.toString()+"  "+members.toString()  );
    }

    @Override
    public void onMemberJoined(LCIMClient client, LCIMConversation conversation, List<String> members, String invitedBy) {
        Log.e(TAG, "onMemberJoined: "+client+"  "+ conversation.toString()+"  "+members.toString()  );

    }

    @Override
    public void onKicked(LCIMClient client, LCIMConversation conversation, String kickedBy) {
        Log.e(TAG, "onKicked: "+client+"  "+ conversation.toString());
    }

    /**
     * 实现本方法来处理当前用户被邀请到某个聊天对话事件
     *
     * @param client
     * @param conversation 被邀请的聊天对话
     * @since 3.0
     */
    @Override
    public void onInvited(LCIMClient client, LCIMConversation conversation, String invitedBy) {
        // 当前 clientId（Jerry）被邀请到对话，执行此处逻辑
        Log.e(TAG, "onInvited: "+ client+"  "+ conversation );
    }
}


// Java/Android SDK 通过定制自己的消息事件 Handler 处理服务端下发的消息通知
 class CustomMessageHandler extends LCIMMessageHandler {
    /**
     * 重载此方法来处理接收消息
     *
     * @param message
     * @param conversation
     * @param client
     */

    @Override
    public void onMessage(LCIMMessage message, LCIMConversation conversation, LCIMClient client){
        if(message instanceof LCIMTextMessage){

            Log.e("TAG", "onMessage: "+ conversation.getCreator());
            Log.e("TAG", "onMessage: "+ conversation.getFetchRequestParams());
            Log.e("TAG", "onMessage: "+ conversation.getMembers());
            Log.e("TAG", "onMessage: "+ conversation.getType());
            Log.e("TAG", "onMessage: "+ conversation.getName());
            Log.e("TAG", "onMessage: "+ conversation.get("msg"));

            EventBus.getDefault().post(MessageEvent.getInstance(((LCIMTextMessage) message).getText(), conversation ));

        }
    }
}
