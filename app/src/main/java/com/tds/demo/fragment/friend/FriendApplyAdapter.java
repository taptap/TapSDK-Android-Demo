package com.tds.demo.fragment.friend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tds.demo.R;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.LCFriendshipRequest;

/**
 * 2022/10/19
 * Describe：好友申请列表的 Adapter
 */
public class FriendApplyAdapter extends RecyclerView.Adapter<FriendApplyAdapter.MyViewHolder>{

    private List<LCFriendshipRequest> lCFriendshipRequestList = new ArrayList<>();
    public static final int handleType_AGREE = 1;
    public static final int handleType_REFUSE = 2;
    public static final int handleType_DEL = 3;

    public void addData(List<LCFriendshipRequest> data){
        if(data !=null){
            lCFriendshipRequestList.clear();
            lCFriendshipRequestList.addAll(data);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_friend, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(lCFriendshipRequestList.get(position) , position);

    }

    @Override
    public int getItemCount() {
        return lCFriendshipRequestList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView nickname;
        private TextView userShortid;
        private TextView agreeApply;
        private TextView refuseApply;
        private TextView delApply;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nickname = itemView.findViewById(R.id.user_nickname);
            userShortid = itemView.findViewById(R.id.user_shortid);
            agreeApply = itemView.findViewById(R.id.agree_apply);
            refuseApply = itemView.findViewById(R.id.refuse_apply);
            delApply = itemView.findViewById(R.id.del_apply);
        }

        public void setData(LCFriendshipRequest request, int position) {

            nickname.setText(request.getSourceUser().getServerData().get("nickname").toString());
            userShortid.setText(request.getSourceUser().getServerData().get("shortId").toString());

            agreeApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(request, position, handleType_AGREE);
                }
            });

            refuseApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(request, position,handleType_REFUSE);
                }
            });

            delApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(request, position,handleType_DEL);

                }
            });
        }
    }

    private OnItemClickListener mOnItemClickListener;


    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener{

        void onClick(LCFriendshipRequest request, int position, int handleType);

    }

}
