package com.tds.demo.fragment.IM;

/**
 * 2022/11/1
 * Describe：聊天界面的 Adapter
 *
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tds.demo.R;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{
    private List<Msg> mMsgList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rihgtMsg;
        ImageView leftImage;
        ImageView rightImage;


        public ViewHolder(View view) {
            super(view);
            leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
            leftMsg = (TextView) view.findViewById(R.id.left_msg);
            rihgtMsg = (TextView) view.findViewById(R.id.right_msg);
            leftImage= (ImageView) view.findViewById(R.id.left_img);
            rightImage= (ImageView) view.findViewById(R.id.right_img);
        }
    }

    public MsgAdapter(List<Msg> msgList) {
        mMsgList = msgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);


        if (msg.getType() == Msg.TYPE_RECEIVED) {
            if(msg.getMsg_type() == Msg.TEXT_TYPE){
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.rightLayout.setVisibility(View.GONE);
                holder.leftMsg.setText(msg.getContent());
                holder.leftImage.setVisibility(View.GONE);

            }else{
                holder.leftLayout.setVisibility(View.GONE);
                holder.rightLayout.setVisibility(View.GONE);
                holder.leftImage.setVisibility(View.VISIBLE);
                Glide.with(holder.leftImage.getContext())
                        .load(msg.getImagePath())
                        .into(holder.leftImage);
            }

        } else if (msg.getType() == Msg.TYPE_SENT) {
            if(msg.getMsg_type() == Msg.TEXT_TYPE){
                holder.rightLayout.setVisibility(View.VISIBLE);
                holder.leftLayout.setVisibility(View.GONE);
                holder.rihgtMsg.setText(msg.getContent());
                holder.rightImage.setVisibility(View.GONE);
            }else {
                holder.rightLayout.setVisibility(View.GONE);
                holder.leftLayout.setVisibility(View.GONE);
                holder.rightImage.setVisibility(View.VISIBLE);
                Glide.with(holder.rightImage.getContext())
                        .load(msg.getImagePath())
                        .into(holder.rightImage);
            }

        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }
}