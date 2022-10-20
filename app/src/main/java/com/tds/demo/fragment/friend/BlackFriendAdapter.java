package com.tds.demo.fragment.friend;

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

/**
 * 2022/10/19
 * Describe：黑名单列表的 Adapter
 */
public class BlackFriendAdapter extends RecyclerView.Adapter<BlackFriendAdapter.MyViewHolder>{

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_black_list, parent,false);
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
        private Button move_black;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            user_icon = itemView.findViewById(R.id.user_icon);
            user_nickname = itemView.findViewById(R.id.user_nickname);
            is_online = itemView.findViewById(R.id.is_online);
            move_black = itemView.findViewById(R.id.move_black);


        }

        public void setData(TDSFriendInfo tDSFriendInfo, int position) {
            Glide.with(user_icon.getContext())
                    .load(tDSFriendInfo.getUser().getServerData().get("avatar"))
                    .transform(new GlideCircleTransform())
                    .into(user_icon);
            user_nickname.setText(tDSFriendInfo.getUser().getServerData().get("nickname").toString());

            is_online.setText(tDSFriendInfo.isOnline() ? "在线" : "离线");

            move_black.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(tDSFriendInfo, position);
                }
            });

        }
    }

    private OnItemClickListener mOnItemClickListener;


    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener{

        void onClick(TDSFriendInfo tDSFriendInfo, int position);

    }

}
