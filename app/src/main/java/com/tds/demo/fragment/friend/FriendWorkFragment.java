package com.tds.demo.fragment.friend;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tds.demo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 2022/10/19
 * Describe：
 */
public class FriendWorkFragment extends DialogFragment {

    @BindView(R.id.recycle_view)
    RecyclerView recycleView;

    private static FriendWorkFragment friendWorkFragment = null;

    public FriendWorkFragment() {

    }

    public static final FriendWorkFragment getInstance(String title, ArrayList<UserBean> users) {
        if (friendWorkFragment == null) {
            friendWorkFragment = new FriendWorkFragment();
        }
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putParcelableArrayList("users", users);
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
        if(getArguments() != null){
            String documentUrl = this.getArguments().getString("title");
            ArrayList<UserBean> users = this.getArguments().getParcelableArrayList("users");

            FriendApplyAdapter friendApplyAdapter = new FriendApplyAdapter();
            friendApplyAdapter.addData(users);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recycleView.setLayoutManager(linearLayoutManager);
            recycleView.setAdapter(friendApplyAdapter);
        }







    }
}
