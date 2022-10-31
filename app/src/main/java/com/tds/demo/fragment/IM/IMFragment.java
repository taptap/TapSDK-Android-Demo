package com.tds.demo.fragment.IM;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tds.demo.R;
import com.tds.demo.fragment.WebViewFragment;
import com.tds.demo.fragment.friend.FriendWorkFragment;
import com.tds.demo.until.ToastUtil;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.LCUser;
import cn.leancloud.im.v2.LCIMClient;
import cn.leancloud.im.v2.LCIMConversation;
import cn.leancloud.im.v2.LCIMException;
import cn.leancloud.im.v2.LCIMMessageManager;
import cn.leancloud.im.v2.callback.LCIMClientCallback;
import cn.leancloud.im.v2.callback.LCIMConversationCallback;
import cn.leancloud.im.v2.callback.LCIMConversationCreatedCallback;
import cn.leancloud.im.v2.messages.LCIMTextMessage;

/**
 * 2022-10-11
 * Describe：即时通讯
 */
public class IMFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "IMFragment";
    @BindView(R.id.close_button)
    ImageButton imageButton;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.create_conversation)
    Button create_conversation;
    @BindView(R.id.send_message)
    Button send_message;

    @BindView(R.id.receive_chient_id)
    EditText receive_chient_id;



    // 登录的 IM 客户端
    private LCIMClient testClient;
    // 测试的会话对象
    private LCIMConversation myConversation;

    // 登录客户端的用户昵称
    private String nickname;


    private static IMFragment iMFragment = null;

    public IMFragment() {

    }

    public static final IMFragment getInstance() {
        if (iMFragment == null) {
            iMFragment = new IMFragment();
        }
        return iMFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.layout_im_fragment, container, false);
        ButterKnife.bind(this, view);

        nickname = LCUser.currentUser().getServerData().get("nickname").toString();

        // Tom 创建了一个 client，用昵称作为 clientId 登录测试
        testClient = LCIMClient.getInstance(nickname);
        // Tom 登录
        testClient.open(new LCIMClientCallback() {
            @Override
            public void done(LCIMClient client, LCIMException e) {
                if (e == null) {
                    // 成功打开连接
                    ToastUtil.showCus("成功打开链接", ToastUtil.Type.SUCCEED);
                }else {
                    Log.e(TAG, "Tom 登录: "+e.getLocalizedMessage() );
                    ToastUtil.showCus(e.getLocalizedMessage(), ToastUtil.Type.ERROR);
                }
            }
        });


        LCIMMessageManager.setConversationEventHandler(new CustomConversationEventHandler());


        LCIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler());

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageButton.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        create_conversation.setOnClickListener(this);
        send_message.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(IMFragment.getInstance()).commit();
                break;
            case R.id.intro_button:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.com/docs/sdk/im/features/"), null)
                        .addToBackStack("webViewFragment")
                        .commit();
                break;

            case R.id.create_conversation:

                createAloneChat();
                break;

            case R.id.send_message:
                sendMessage("");
                break;

            default:
                break;

        }

    }

    private void createAloneChat() {
        if(receive_chient_id.getText().toString().isEmpty() || receive_chient_id.getText().toString().trim().isEmpty() ){
            ToastUtil.showCus("输入接收消息方的Tap账号昵称", ToastUtil.Type.POINT);
            return;
        }

        testClient.createConversation(Arrays.asList(receive_chient_id.getText().toString().trim()), receive_chient_id.getText().toString().trim(), null, false, true,
                new LCIMConversationCreatedCallback() {
                    @Override
                    public void done(LCIMConversation conversation, LCIMException e) {
                        if(e == null) {
                            // 创建成功
                            myConversation = conversation;

                            ChatBean chatBean = new ChatBean();
                            chatBean.setConversation(conversation);
                            chatBean.setNickname(receive_chient_id.getText().toString().trim());

                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, AloneChatFragment.getInstance(chatBean), null)
                                    .addToBackStack("aloneChatFragment")
                                    .commit();

                        }else{
                            Log.e(TAG, "Tom 创建 : "+e.getLocalizedMessage() );
                            ToastUtil.showCus(e.getLocalizedMessage(), ToastUtil.Type.ERROR);
                        }
                    }
                });
    }


    /**
     * 发送消息
     *
     **/
    private void sendMessage(String message) {
        LCIMTextMessage msg = new LCIMTextMessage();
        msg.setText(message);
        // 发送消息
        myConversation.sendMessage(msg, new LCIMConversationCallback() {
            @Override
            public void done(LCIMException e) {
                if (e == null) {
                    Log.d(TAG, "发送成功！");
                }
            }
        });

    }


}
