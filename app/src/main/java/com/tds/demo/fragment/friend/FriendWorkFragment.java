package com.tds.demo.fragment.friend;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.tds.demo.until.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.LCFriendshipRequest;

/**
 * 2022/10/19
 * Describe：
 */
public class FriendWorkFragment extends DialogFragment {

    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.close_button)
    ImageButton close_button;
    @BindView(R.id.no_content)
    TextView no_content;
    @BindView(R.id.title)
    TextView title;

    private static FriendWorkFragment friendWorkFragment = null;

    public FriendWorkFragment() {

    }

    public static final FriendWorkFragment getInstance(String title, FriendBean friendBean) {
        if (friendWorkFragment == null) {
            friendWorkFragment = new FriendWorkFragment();
        }
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putSerializable("requests", friendBean);
        friendWorkFragment.setArguments(args);
        return friendWorkFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.friends_dialog_fragment, container, false);
        ButterKnife.bind(this, view);


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack("friendWorkFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
        });


        if(getArguments() != null) {
            String titleContent = this.getArguments().getString("title");
            title.setText(titleContent);

            FriendBean friendBean = (FriendBean) this.getArguments().getSerializable("requests");

            if (friendBean != null && (friendBean.getRequests().size() > 0 || friendBean.gettDSFriendInfo().size() > 0)) {
                no_content.setVisibility(View.GONE);
            }

            if (titleContent.equals("好友申请列表")) {
                FriendApplyAdapter friendApplyAdapter = new FriendApplyAdapter();
                friendApplyAdapter.addData(friendBean.getRequests());

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recycleView.setLayoutManager(linearLayoutManager);
                recycleView.setAdapter(friendApplyAdapter);
                friendApplyAdapter.setmOnItemClickListener((request, position, handleType) -> {
                    // 同意
                    if (handleType == FriendApplyAdapter.handleType_AGREE) {
                        AgreeInvite(request);
                    } else if (handleType == FriendApplyAdapter.handleType_REFUSE) {
                        // 拒绝
                        refuseInvite(request);

                    } else {
                        // 删除
                        delInvite(request);
                    }

                });
            }
            else if(titleContent.equals("好友列表")){
                FriendListAdapter friendListAdapter = new FriendListAdapter();
                friendListAdapter.addData(friendBean.gettDSFriendInfo());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recycleView.setLayoutManager(linearLayoutManager);
                recycleView.setAdapter(friendListAdapter);
                friendListAdapter.setmOnItemClickListener(new FriendListAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(TDSFriendInfo tDSFriendInfo, int position, int handleType) {
                        if(handleType == FriendListAdapter.handleType_DEL){
                            // 删除
                            delFriend(tDSFriendInfo);
                        }else{
                            // 拉黑
                            blackFriend(tDSFriendInfo);
                        }
                    }
                });
            }
            else if (titleContent.equals("黑名单列表")){
                BlackFriendAdapter blackFriendAdapter = new BlackFriendAdapter();
                blackFriendAdapter.addData(friendBean.gettDSFriendInfo());
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recycleView.setLayoutManager(linearLayoutManager);
                recycleView.setAdapter(blackFriendAdapter);
                blackFriendAdapter.setmOnItemClickListener(new BlackFriendAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(TDSFriendInfo tDSFriendInfo, int position) {
                         moveBlackFriend(tDSFriendInfo);
                    }
                });

            }
        }

    }

    /**
     *
     * 移出黑名单用户
     *
     * */
    private void moveBlackFriend(TDSFriendInfo tDSFriendInfo) {
        TDSFriends.unblockFriend(tDSFriendInfo.getUser().get("objectId").toString(), new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                ToastUtil.showCus("移出黑名单", ToastUtil.Type.SUCCEED);

            }

            @Override
            public void onFail(TDSFriendError error) {
                ToastUtil.showCus(error.detailMessage, ToastUtil.Type.ERROR);
            }
        });
    }


    /**
     * 拉黑好友
     * */
    private void blackFriend(TDSFriendInfo tDSFriendInfo) {

        TDSFriends.blockFriend(tDSFriendInfo.getUser().get("objectId").toString(), new Callback<Void>(){
            @Override
            public void onSuccess(Void result) {
                ToastUtil.showCus("拉黑好友", ToastUtil.Type.SUCCEED);
            }
            @Override
            public void onFail(TDSFriendError error) {
                ToastUtil.showCus(error.detailMessage, ToastUtil.Type.ERROR);
            }
        });
    }

    /**
     * 删除好友
     * */
    private void delFriend(TDSFriendInfo tDSFriendInfo) {
        TDSFriends.deleteFriend(tDSFriendInfo.getUser().get("objectId").toString(), new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                ToastUtil.showCus("删除好友", ToastUtil.Type.SUCCEED);
            }

            @Override
            public void onFail(TDSFriendError error) {
                ToastUtil.showCus(error.detailMessage, ToastUtil.Type.ERROR);
            }
        });

    }


    /**
     * 拒绝好友邀请
     *
     * */
    private void refuseInvite(LCFriendshipRequest request) {
        TDSFriends.declineFriendRequest(request, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                ToastUtil.showCus("拒绝邀请", ToastUtil.Type.SUCCEED);
            }

            @Override
            public void onFail(TDSFriendError error) {
                ToastUtil.showCus(error.detailMessage, ToastUtil.Type.ERROR);

            }
        });
    }

    /**
     * 同意好友邀请
     *
     * */
    private void AgreeInvite(LCFriendshipRequest request) {
        TDSFriends.acceptFriendRequest(request, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                ToastUtil.showCus("接受邀请", ToastUtil.Type.SUCCEED);
            }

            @Override
            public void onFail(TDSFriendError error) {
                ToastUtil.showCus(error.detailMessage, ToastUtil.Type.ERROR);

            }
        });
    }

    /**
     * 删除好友邀请
     *
     * */
    private void delInvite(LCFriendshipRequest request) {
        TDSFriends.deleteFriendRequest(request, new Callback<Boolean>() {

            @Override
            public void onSuccess(Boolean aBoolean) {
                ToastUtil.showCus("删除邀请", ToastUtil.Type.SUCCEED);
            }

            @Override
            public void onFail(TDSFriendError tdsFriendError) {
                ToastUtil.showCus(tdsFriendError.detailMessage, ToastUtil.Type.ERROR);

            }
        });
    }













}
