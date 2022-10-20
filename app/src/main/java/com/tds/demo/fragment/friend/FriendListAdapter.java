package com.tds.demo.fragment.friend;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tapsdk.friends.entities.TDSFriendInfo;
import com.tds.demo.R;
import com.tds.demo.until.GlideCircleTransform;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.LCFriendshipRequest;

/**
 * 2022/10/19
 * Describe：好友列表的 Adapter
 */
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder>{

    private List<TDSFriendInfo> tDSFriendInfoList = new ArrayList<>();
    public static final int handleType_DEL = 1;
    public static final int handleType_BLACK = 2;
    public void addData(List<TDSFriendInfo> data){
        if(data !=null){
            tDSFriendInfoList.clear();
            tDSFriendInfoList.addAll(data);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_list, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(tDSFriendInfoList.get(position) , position);

    }

    @Override
    public int getItemCount() {
        return tDSFriendInfoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView user_icon;
        private TextView user_nickname;
        private TextView is_online;
        private Button del_friend;
        private Button black_friend;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            user_icon = itemView.findViewById(R.id.user_icon);
            user_nickname = itemView.findViewById(R.id.user_nickname);
            is_online = itemView.findViewById(R.id.is_online);
            del_friend = itemView.findViewById(R.id.del_friend);
            black_friend = itemView.findViewById(R.id.black_friend);

        }

        public void setData(TDSFriendInfo tDSFriendInfo, int position) {
            Glide.with(user_icon.getContext())
                    .load(tDSFriendInfo.getUser().getServerData().get("avatar"))
                    .transform(new GlideCircleTransform())
                    .into(user_icon);
            user_nickname.setText(tDSFriendInfo.getUser().getServerData().get("nickname").toString());

            is_online.setText(tDSFriendInfo.isOnline() ? "在线" : "离线");

            del_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(tDSFriendInfo, position, handleType_DEL);
                }
            });

            black_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(tDSFriendInfo, position, handleType_BLACK);

                }
            });

        }
    }

    private OnItemClickListener mOnItemClickListener;


    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener{

        void onClick(TDSFriendInfo tDSFriendInfo, int position, int handleType);

    }

}
