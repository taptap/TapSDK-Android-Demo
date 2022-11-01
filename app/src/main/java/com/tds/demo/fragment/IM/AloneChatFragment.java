package com.tds.demo.fragment.IM;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tds.demo.R;
import com.tds.gson.JsonObject;
import com.tds.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.LCUser;
import cn.leancloud.im.v2.LCIMConversation;
import cn.leancloud.im.v2.LCIMException;
import cn.leancloud.im.v2.LCIMMessage;
import cn.leancloud.im.v2.LCIMMessageManager;
import cn.leancloud.im.v2.callback.LCIMConversationCallback;
import cn.leancloud.im.v2.callback.LCIMMessagesQueryCallback;
import cn.leancloud.im.v2.messages.LCIMTextMessage;

/**
 * 2022/10/19
 * Describe：单聊
 */
public class AloneChatFragment extends DialogFragment {

    @BindView(R.id.close_button)
    ImageButton close_button;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.send_message)
    EditText send_message;
    @BindView(R.id.message_recycle)
    RecyclerView message_recycle;


    private LCIMConversation conversation;

    private static AloneChatFragment aloneChatFragment = null;
    private List<Msg> msgList = new ArrayList<>();
    private MsgAdapter adapter = null;
    public AloneChatFragment() {

    }

    public static final AloneChatFragment getInstance(ChatBean chatBean) {
        if (aloneChatFragment == null) {
            aloneChatFragment = new AloneChatFragment();
        }
        Bundle args = new Bundle();
        args.putSerializable("chatBean", chatBean);
        aloneChatFragment.setArguments(args);
        return aloneChatFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.friends_alone_chat_fragment, container, false);
        ButterKnife.bind(this, view);


        LCIMMessageManager.setConversationEventHandler(new CustomConversationEventHandler());
        LCIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler());




        return view;
    }


    /**
     * 根据 ObjectId查询消息
     *
     * */
    public void searchMsg(){

        // limit 取值范围 1~100，如调用 queryMessages 时不带 limit 参数，默认获取 20 条消息记录
        int limit = 100;
        conversation.queryMessages(limit, new LCIMMessagesQueryCallback() {
            @Override
            public void done(List<LCIMMessage> messages, LCIMException e) {
                if (e == null) {
                    for(int i=0; i< messages.size(); i++){
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(messages.get(i).getContent());
                        if(messages.get(i).getFrom().equals(LCUser.currentUser().getServerData().get("nickname").toString())){
                            Msg received_content = new Msg( jsonObject.get("_lctext").toString() , Msg.TYPE_SENT);
                            msgList.add(received_content);
                        }else{
                            Msg received_content = new Msg( jsonObject.get("_lctext").toString() , Msg.TYPE_RECEIVED);
                            msgList.add(received_content);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments() != null) {
            ChatBean chatBean = (ChatBean) this.getArguments().getSerializable("chatBean");
            conversation = chatBean.getConversation();
            title.setText( conversation.getMembers().get(0)+"与"+conversation.getMembers().get(1));

        }

        searchMsg();

        // 监听消息发送的按钮
        send_message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND){
                    if(!send_message.getText().toString().isEmpty()){
                        sendMessage(conversation, send_message.getText().toString());
                        send_message.setText("");
                    }
                    return true;
                }
                return false;
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        message_recycle.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        message_recycle.setAdapter(adapter);


        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack("aloneChatFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this); // 将当前Activity绑定为订阅者

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this); // 解绑
        super.onStop();

    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(MessageEvent messageEvent) {
        Log.d("TAG", "onEvent() called with: messageEvent = [" + messageEvent.getMessage() + "]");

        Msg received_content = new Msg( messageEvent.getMessage(), Msg.TYPE_RECEIVED);
        msgList.add(received_content);
        adapter.notifyDataSetChanged();
    }

    /**
     * 发送消息
     *
     **/
    private void sendMessage(LCIMConversation conversation, String message) {
        LCIMTextMessage msg = new LCIMTextMessage();
        msg.setText(message);
        // 发送消息
        conversation.sendMessage(msg, new LCIMConversationCallback() {
            @Override
            public void done(LCIMException e) {
                if (e == null) {
                    Msg send_content = new Msg(message, Msg.TYPE_SENT);
                    msgList.add(send_content);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }
}
