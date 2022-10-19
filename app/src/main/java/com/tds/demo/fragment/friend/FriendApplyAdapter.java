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

/**
 * 2022/10/19
 * Describe：
 */
public class FriendApplyAdapter extends RecyclerView.Adapter<FriendApplyAdapter.MyViewHolder>{

    private List<UserBean> users = new ArrayList<>();

    public void addData(List<UserBean> data){
        if(data !=null){
            users.clear();
            users.addAll(data);
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
        holder.setData(users.get(position) , position);

    }

    @Override
    public int getItemCount() {
        return users.size();
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

        public void setData(UserBean userBean, int position) {

            nickname.setText(userBean.getNickname());
            userShortid.setText(userBean.getShortId());
//                  agreeApply
//            refuseApply
//                    delApply
        }
    }
}
