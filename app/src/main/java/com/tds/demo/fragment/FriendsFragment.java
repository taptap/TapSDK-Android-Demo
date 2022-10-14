package com.tds.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tapsdk.friends.FriendStatusChangedListener;
import com.tapsdk.friends.TDSFriends;
import com.tapsdk.friends.entities.TDSFriendInfo;
import com.tapsdk.friends.entities.TDSFriendshipRequest;
import com.tapsdk.friends.entities.TDSRichPresence;
import com.tds.demo.R;
import com.tds.demo.until.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 2022/10/14
 * Describe：游戏内好友
 */
public class FriendsFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.close_button)
    ImageButton close_button;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.friend_listener)
    Button friend_listener;
    @BindView(R.id.stop_listener)
    Button stop_listener;



    private static FriendsFragment friendsFragment = null;

    public FriendsFragment() {

    }

    public static final FriendsFragment getInstance() {
        if (friendsFragment == null) {
            friendsFragment = new FriendsFragment();
        }
        return friendsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.friends_fragment, container, false);
        ButterKnife.bind(this, view);




        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        close_button.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        friend_listener.setOnClickListener(this);
        stop_listener.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(FriendsFragment.getInstance()).commit();

                break;
            case R.id.intro_button:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.com/docs/sdk/friends/features/"), null)
                        .addToBackStack("webViewFragment")
                        .commit();
                break;
            case R.id.friend_listener:
                initListener();
                break;

            case R.id.stop_listener:
                stopListenr();
                break;


            default:
                break;
        }
    }





    /**
     * 响应好友变化通知
     * 好友模块支持客户端监听好友状态变化，在游戏中实时给玩家提示。 你需要在调用上线接口前注册好友状态变更监听实例，这样，玩家上线后就能收到相应通知
     */
    private void initListener() {

        TDSFriends.registerFriendStatusChangedListener(new FriendStatusChangedListener() {
            // 新增好友（触发时机同「已发送的好友申请被接受」）
            @Override
            public void onFriendAdd(TDSFriendInfo friendInfo) {
                ToastUtil.showCus(friendInfo.getUser().getUsername()+"新增好友", ToastUtil.Type.POINT);
            }

            // 新增好友申请
            @Override
            public void onNewRequestComing(TDSFriendshipRequest request) {
                ToastUtil.showCus(request.getFriendInfo().toString()+"新增好友申请", ToastUtil.Type.POINT);
            }

            // 通过分享链接进入游戏时触发此回调
            // 开发者可以在此回调中直接调用 handFriendInvitationLink
            // 或通过 parseFriendInvitationLink 解析链接，获取相关参数再执行自定义的逻辑
            @Override
            public void onReceivedInvitationLink(String url) {
                ToastUtil.showCus("邀请好友成功！", ToastUtil.Type.POINT);
            }

            // 已发送的好友申请被接受
            @Override
            public void onRequestAccepted(TDSFriendshipRequest request) {
                ToastUtil.showCus(request.getFriendInfo().getUser().getUsername()+"同意你的好友申请", ToastUtil.Type.POINT);
            }

            // 已发送的好友申请被拒绝
            @Override
            public void onRequestDeclined(TDSFriendshipRequest request) {
                ToastUtil.showCus(request.getFriendInfo().getUser().getUsername()+"拒绝你的好友申请", ToastUtil.Type.POINT);
            }

            // 好友上线
            @Override
            public void onFriendOnline(String userId) {
                ToastUtil.showCus("userID："+userId+" 好友上线", ToastUtil.Type.POINT);

            }

            // 好友下线
            @Override
            public void onFriendOffline(String userId) {
                ToastUtil.showCus("userID："+userId+" 好友下线", ToastUtil.Type.POINT);
            }

            // 好友富信息变更
            @Override
            public void onRichPresenceChanged(String userId, TDSRichPresence richPresence) {
                ToastUtil.showCus("userID："+userId+" 好友富信息变更", ToastUtil.Type.POINT);
            }

            // 当前玩家成功上线（长连接建立成功）
            @Override
            public void onConnected() {
                ToastUtil.showCus("当前玩家成功上线", ToastUtil.Type.POINT);
            }

            // 当前玩家长连接断开，SDK 会自动重试，开发者通常无需额外处理
            @Override
            public void onDisconnected() {
                ToastUtil.showCus("当前玩家长连接断开", ToastUtil.Type.POINT);
            }

            // 当前连接异常
            @Override
            public void onConnectError(int code, String msg){
                ToastUtil.showCus("当前连接异常", ToastUtil.Type.POINT);
            }
        });
    }


    /**
     * 停止监听好友状态的变化
     * */
    private void stopListenr() {
        TDSFriends.removeFriendStatusChangedListener();

    }


}
