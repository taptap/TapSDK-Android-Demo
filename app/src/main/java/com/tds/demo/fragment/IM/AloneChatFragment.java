package com.tds.demo.fragment.IM;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapsdk.friends.Callback;
import com.tapsdk.friends.TDSFriends;
import com.tapsdk.friends.entities.TDSFriendInfo;
import com.tapsdk.friends.exceptions.TDSFriendError;
import com.tds.demo.R;
import com.tds.demo.fragment.friend.BlackFriendAdapter;
import com.tds.demo.fragment.friend.FriendApplyAdapter;
import com.tds.demo.fragment.friend.FriendBean;
import com.tds.demo.fragment.friend.FriendListAdapter;
import com.tds.demo.until.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.LCFriendshipRequest;
import cn.leancloud.im.v2.LCIMConversation;
import cn.leancloud.im.v2.LCIMException;
import cn.leancloud.im.v2.LCIMMessageManager;
import cn.leancloud.im.v2.callback.LCIMConversationCallback;
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


    private LCIMConversation conversation;

    private static AloneChatFragment aloneChatFragment = null;

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


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments() != null) {
            ChatBean chatBean = (ChatBean) this.getArguments().getSerializable("chatBean");
            title.setText(chatBean.getNickname());
            conversation = chatBean.getConversation();
        }


        send_message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND){
                    if(!send_message.getText().toString().isEmpty()){
                        sendMessage(conversation, send_message.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });



        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack("aloneChatFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
        });

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
                    Log.d("TAG", "发送成功！");
                }
            }
        });

    }




}
