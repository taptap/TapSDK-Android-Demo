package com.tds.demo.fragment.friend;

import static com.tds.demo.data.SDKInfoData.Clound_SHAREHOST;

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

import com.tapsdk.bootstrap.account.TDSUser;
import com.tapsdk.friends.Callback;
import com.tapsdk.friends.FriendStatusChangedListener;
import com.tapsdk.friends.ListCallback;
import com.tapsdk.friends.TDSFriends;
import com.tapsdk.friends.entities.TDSFriendInfo;
import com.tapsdk.friends.entities.TDSFriendLinkInfo;
import com.tapsdk.friends.entities.TDSFriendshipRequest;
import com.tapsdk.friends.entities.TDSRichPresence;
import com.tapsdk.friends.exceptions.TDSFriendError;
import com.tds.demo.R;
import com.tds.demo.fragment.WebViewFragment;
import com.tds.demo.until.PlatformUtil;
import com.tds.demo.until.ToastUtil;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.LCFriendshipRequest;

/**
 * 2022/10/19
 * Describe：好友功能
 */
public class FriendsFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.close_button)
    ImageButton close_button;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.apply_list)
    Button apply_list;
    @BindView(R.id.friend_list)
    Button friend_list;
    @BindView(R.id.black_list)
    Button black_list;
    @BindView(R.id.shortid_edit)
    EditText shortid_edit;
    @BindView(R.id.add_for_shortid)
    Button add_for_shortid;
    @BindView(R.id.objectid_edit)
    EditText objectid_edit;
    @BindView(R.id.add_for_objectid)
    Button add_for_objectid;
    @BindView(R.id.share_url)
    Button share_url;
    @BindView(R.id.nickname_edit)
    EditText nickname_edit;
    @BindView(R.id.search_for_nickname)
    Button search_for_nickname;
    @BindView(R.id.shortid_search_edit)
    EditText shortid_search_edit;
    @BindView(R.id.search_for_shortid)
    Button search_for_shortid;
    @BindView(R.id.objectid_search_edit)
    EditText objectid_search_edit;
    @BindView(R.id.search_for_objectid)
    Button search_for_objectid;
    @BindView(R.id.player_up)
    Button player_up;
    @BindView(R.id.player_down)
    Button player_down;
    @BindView(R.id.stop_listener)
    Button stop_listener;








    private static FriendsFragment friendsFragment = null;
    private FriendBean friendBean= null;

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
        View view= inflater.inflate(R.layout.friends_new_fragment, container, false);
        ButterKnife.bind(this, view);
        // 好友状态监听
        initListener();

        // 落地页网站的地址需要在客户端配置：
        TDSFriends.setShareLink(Clound_SHAREHOST);



        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        close_button.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        apply_list.setOnClickListener(this);
        friend_list.setOnClickListener(this);
        black_list.setOnClickListener(this);
        add_for_shortid.setOnClickListener(this);
        add_for_objectid.setOnClickListener(this);
        share_url.setOnClickListener(this);
        search_for_nickname.setOnClickListener(this);
        search_for_shortid.setOnClickListener(this);
        search_for_objectid.setOnClickListener(this);
        player_up.setOnClickListener(this);
        player_down.setOnClickListener(this);
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
            case R.id.apply_list:
                searchApplyList();
                break;
            case R.id.friend_list:
                searchFriendList();
                break;
            case R.id.black_list:
                searchBlackList();
                break;
            case R.id.add_for_shortid:
                addFriendForShortId();
                break;
            case R.id.add_for_objectid:
                addFriendForObjectId();
                break;
            case R.id.share_url:
                shareInviteUrl();
                break;
            case R.id.search_for_nickname:
                searchForNickname();
                break;
            case R.id.search_for_shortid:
                searchForShortid();
                break;
            case R.id.search_for_objectid:
                searchForObjectid();
                break;
            case R.id.player_up:
                playUp();
                break;
            case R.id.player_down:
                playerDown();
                break;
            case R.id.stop_listener:
                stopListenr();
                break;


            default:
                break;
        }
    }





    /**
     * 通过 ObjectId 查询好友
     * */
    private void searchForObjectid() {
        if(objectid_search_edit.getText().toString().isEmpty()){
            ToastUtil.showCus("请输入ObjectId！", ToastUtil.Type.POINT);
            return;
        }
        TDSFriends.searchUserById(objectid_search_edit.getText().toString(), new Callback<TDSFriendInfo>() {
            @Override
            public void onSuccess(TDSFriendInfo friendInfo) {
                ToastUtil.showCus("好友昵称是："+friendInfo.getUser().getUsername(), ToastUtil.Type.SUCCEED);

            }

            @Override
            public void onFail(TDSFriendError error) {

                ToastUtil.showCus(error.detailMessage, ToastUtil.Type.ERROR);
            }
        });

    }


    /**
     * 通过好友码查询好友
     * */
    private void searchForShortid() {
        if(shortid_search_edit.getText().toString().isEmpty()){
            ToastUtil.showCus("请输入好友码！", ToastUtil.Type.POINT);
            return;
        }
        TDSFriends.searchUserByShortCode(shortid_search_edit.getText().toString(), new Callback<TDSFriendInfo>() {
            @Override
            public void onSuccess(TDSFriendInfo friendInfo) {
                /* 略（参见上节） */
                ToastUtil.showCus("好友昵称是："+friendInfo.getUser().getUsername(), ToastUtil.Type.SUCCEED);

            }

            @Override
            public void onFail(TDSFriendError error) {

                ToastUtil.showCus(error.detailMessage, ToastUtil.Type.ERROR);
            }
        });

    }


    /**
     * 通过用户昵称查询好友
     * */
    private void searchForNickname() {
        if(nickname_edit.getText().toString().isEmpty()){
            ToastUtil.showCus("请输入用户昵称！", ToastUtil.Type.POINT);
            return;
        }
        TDSFriends.searchUserByName(nickname_edit.getText().toString(), new ListCallback<TDSFriendInfo>() {
            @Override
            public void onSuccess(List<TDSFriendInfo> friendInfoList) {
                for (TDSFriendInfo info : friendInfoList) {
                    // 玩家信息
                    TDSUser user = info.getUser();
                    // 富信息数据，详见后文
                    TDSRichPresence richPresence = info.getRichPresence();
                    // 好友是否在线
                    boolean online = info.isOnline();
                }
                ToastUtil.showCus("好友昵称是："+friendInfoList.get(0).getUser().getUsername(), ToastUtil.Type.SUCCEED);

            }

            @Override
            public void onFail(TDSFriendError error) {
                ToastUtil.showCus(error.detailMessage, ToastUtil.Type.ERROR);
            }
        });

    }


    /**
     * 先生成邀请链接，然后进行分享
     * */
    private void shareInviteUrl() {
        // 生成链接
            TDSFriends.generateFriendInvitationLink(new Callback<String>() {
                @Override
                public void onSuccess(String inviteUrl) {
                    Log.e("TAG", "onSuccess:====》 "+ inviteUrl);

                    // 分享功能，需开发者自行实现
                    ToastUtil.showCus("分享链接"+inviteUrl, ToastUtil.Type.SUCCEED);
                    if (PlatformUtil.isInstallApp(getActivity(),PlatformUtil.PACKAGE_WECHAT )){
                        PlatformUtil.shareWechatFriend(getActivity(), inviteUrl );
                    }else if (PlatformUtil.isInstallApp(getActivity(),PlatformUtil.PACKAGE_MOBILE_QQ )){
                        PlatformUtil.shareQQ(getActivity(),  inviteUrl);
                    }


                }
                @Override
                public void onFail(TDSFriendError error) {
                    if (error.code == 9304){
                        ToastUtil.showCus("落地页网站的地址需要在客户端配置", ToastUtil.Type.WARNING);
                    }else{
                        ToastUtil.showCus(error.toJSON(), ToastUtil.Type.ERROR);
                    }

                }
            });
    }

    /**
     * 通过 ObjectId 添加好友
     *
     * */
    private void addFriendForObjectId() {
        if (objectid_edit.getText().toString().isEmpty()){
            ToastUtil.showCus("请输入用户 ObjectId！", ToastUtil.Type.POINT);
            return;
        }
        TDSFriends.addFriend(objectid_edit.getText().toString(), new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                ToastUtil.showCus("添加好友", ToastUtil.Type.SUCCEED);
            }

            @Override
            public void onFail(TDSFriendError error) {
                ToastUtil.showCus(error.detailMessage, ToastUtil.Type.ERROR);
            }
        });
    }


    /**
     * 通过 shortId 添加好友
     *
     * */
    private void addFriendForShortId() {
        if (shortid_edit.getText().toString().isEmpty()){
            ToastUtil.showCus("请输入好友码！", ToastUtil.Type.POINT);
            return;
        }
        TDSFriends.addFriendByShortCode(shortid_edit.getText().toString(), null, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                ToastUtil.showCus("添加好友", ToastUtil.Type.SUCCEED);
            }

            @Override
            public void onFail(TDSFriendError error) {
                ToastUtil.showCus(error.detailMessage, ToastUtil.Type.ERROR);
            }
        });

    }


    /**
     * 查询黑名单列表
     * */
    private void searchBlackList() {

        TDSFriends.queryBlockList(0, 100, new ListCallback<TDSFriendInfo>() {
            @Override
            public void onSuccess(List<TDSFriendInfo> result) {
                friendBean= new FriendBean();
                friendBean.settDSFriendInfo(result);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, FriendWorkFragment.getInstance("黑名单列表", friendBean), null)
                        .addToBackStack("friendWorkFragment")
                        .commit();


            }

            @Override
            public void onFail(TDSFriendError error) {
                ToastUtil.showCus(error.detailMessage, ToastUtil.Type.SUCCEED);
            }
        });

    }


    /**
     * 查询好友列表
     * */
    private void searchFriendList() {

        int from = 0;
        int limit = 100;
        TDSFriends.queryFriendList(from, limit, new ListCallback<TDSFriendInfo>(){
                    @Override
                    public void onSuccess(List<TDSFriendInfo> friendInfoList) {
//                        for (TDSFriendInfo info : friendInfoList) {
//                            // 玩家信息
//                            TDSUser user = info.getUser();
//                            // 富信息数据
//                            TDSRichPresence richPresence = info.getRichPresence();
//                            // 好友是否在线
//                            boolean online = info.isOnline();
//                        }

                        friendBean= new FriendBean();
                        friendBean.settDSFriendInfo(friendInfoList);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, FriendWorkFragment.getInstance("好友列表", friendBean), null)
                                .addToBackStack("friendWorkFragment")
                                .commit();

                    }

                    @Override
                    public void onFail(TDSFriendError error) {
                        ToastUtil.showCus(error.detailMessage, ToastUtil.Type.ERROR);
                    }
                });
    }



    /**
     * 查询好友申请列表
     * */
    private void searchApplyList() {
        int from = 0;
        int limit = 100;
        TDSFriends.queryFriendRequestList(LCFriendshipRequest.STATUS_PENDING, from, limit, new ListCallback<LCFriendshipRequest>(){

                    @Override
                    public void onSuccess(List<LCFriendshipRequest> requests) {
                        // requests 就是处于 pending 状态中的好友申请列表
                        friendBean= new FriendBean();
                        friendBean.setRequests(requests);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, FriendWorkFragment.getInstance("好友申请列表", friendBean), null)
                                .addToBackStack("friendWorkFragment")
                                .commit();



                    }

                    @Override
                    public void onFail(TDSFriendError error) {
                        ToastUtil.showCus(error.detailMessage, ToastUtil.Type.ERROR);
                    }
                });

    }


    /**
     * 玩家下线
     * */
    private void playerDown() {
        TDSFriends.offline();
    }


    /**
     * 玩家上线
     * */
    private void playUp() {
        TDSFriends.online(new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // 成功
                ToastUtil.showCus("玩家上线", ToastUtil.Type.SUCCEED);
            }

            @Override
            public void onFail(TDSFriendError error) {
                // 处理异常
                ToastUtil.showCus(error.detailMessage, ToastUtil.Type.ERROR);

            }
        });
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
                // 玩家通过邀请链接打开游戏后，开发者需要调用该接口。 调用该接口后，SDK 会自动向对应的玩家发起好友申请。
                TDSFriends.handFriendInvitationLink(url, new Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {

                        // 通过邀请链接进行数据解析

                        TDSFriendLinkInfo linkInfo = TDSFriends.parseFriendInvitationLink(url);
                        String userObjectId = linkInfo.getIdentity();
                        String name = linkInfo.getRoleName();
                        Map<String, String> parameters = linkInfo.getQueries();

                        ToastUtil.showCus("通过'"+name+"'邀请链接打开游戏成功：", ToastUtil.Type.SUCCEED);
                    }

                    @Override
                    public void onFail(TDSFriendError error) {
                        ToastUtil.showCus("通过邀请链接打开游戏失败："+error.detailMessage, ToastUtil.Type.ERROR);

                    }
                });


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
