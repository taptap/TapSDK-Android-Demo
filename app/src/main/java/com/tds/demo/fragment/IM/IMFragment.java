package com.tds.demo.fragment.IM;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.tds.demo.R;
import com.tds.demo.fragment.WebViewFragment;
import com.tds.demo.until.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.LCUser;
import cn.leancloud.im.v2.LCIMClient;
import cn.leancloud.im.v2.LCIMConversation;
import cn.leancloud.im.v2.LCIMException;
import cn.leancloud.im.v2.LCIMMessageManager;
import cn.leancloud.im.v2.callback.LCIMClientCallback;
import cn.leancloud.im.v2.callback.LCIMConversationCreatedCallback;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

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
    @BindView(R.id.new_msg_notify)
    Button new_msg_notify;
    @BindView(R.id.receive_chient_id)
    EditText receive_chient_id;
    @BindView(R.id.new_msg)
    ConstraintLayout new_msg;

    private MessageEvent myMessageEvent;

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

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.layout_im_fragment, container, false);
        ButterKnife.bind(this, view);

        nickname = LCUser.currentUser().getServerData().get("nickname").toString();

//          创建了一个 client，用昵称作为 clientId 登录测试
        testClient = LCIMClient.getInstance(nickname);
        //  登录
        testClient.open(new LCIMClientCallback() {
            @Override
            public void done(LCIMClient client, LCIMException e) {
                if (e == null) {
                    // 成功打开连接
                    ToastUtil.showCus("登录成功，长链接建立成功！", ToastUtil.Type.SUCCEED);
                    Log.e(TAG, "登录成功，长链接建立成功！");
                }else {
                    ToastUtil.showCus(e.getLocalizedMessage(), ToastUtil.Type.ERROR);
                    Log.e(TAG, "done-error: "+ e.getLocalizedMessage());
                }
            }
        });

        // 以 LCUser 的用户名和密码登录到存储服务
//        LCUser.logIn("Tom", "cat!@#123").subscribe(new Observer<LCUser>() {
//            public void onSubscribe(Disposable disposable) {}
//            public void onNext(LCUser user) {
//                // 登录成功，与服务器连接
//                LCIMClient client = LCIMClient.getInstance(user);
//                client.open(new LCIMClientCallback() {
//                    @Override
//                    public void done(final LCIMClient avimClient, LCIMException e) {
//                        // 执行其他逻辑
//                        testClient = avimClient;
//                        Log.e(TAG, "===== done: "+ e+"   >>> "+avimClient );
//                    }
//                });
//            }
//            public void onError(Throwable throwable) {
//                // 登录失败（可能是密码错误）
//                Log.e(TAG, "===== done错误: "+ throwable.getLocalizedMessage());
//
//            }
//            public void onComplete() {}
//        });


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
        new_msg.setOnClickListener(this);
        new_msg_notify.setOnClickListener(this);
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
            case R.id.new_msg_notify:
                newMsg(myMessageEvent);
                break;

            default:
                break;

        }

    }


    /**
    *
    * */
    private void newMsg(MessageEvent messageEvent) {

        ChatBean chatBean = new ChatBean();
        chatBean.setConversation(messageEvent.getlCIMConversation());
        chatBean.setNickname(messageEvent.getlCIMConversation().getCreator());

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, AloneChatFragment.getInstance(chatBean), null)
                .addToBackStack("aloneChatFragment")
                .commit();
    }

    private void createAloneChat() {
        if(receive_chient_id.getText().toString().isEmpty() || receive_chient_id.getText().toString().trim().isEmpty() ){
            ToastUtil.showCus("输入接收消息方的Tap账号昵称", ToastUtil.Type.POINT);
            return;
        }

        testClient.createConversation(Arrays.asList(receive_chient_id.getText().toString().trim()), nickname+"与"+receive_chient_id.getText().toString().trim(), null, false, true,
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

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onMessageEvent(MessageEvent messageEvent) {
        new_msg.setVisibility(View.VISIBLE);
        myMessageEvent = messageEvent;
    }


}
