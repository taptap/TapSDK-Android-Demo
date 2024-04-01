package com.tds.demo.fragment.achievement;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tds.achievement.TapAchievementBean;
import com.tds.demo.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 2022/10/19
 * Describe：好友申请列表的 Adapter
 */
public class ArchievementAdapter extends RecyclerView.Adapter<ArchievementAdapter.MyViewHolder>{

    private List<TapAchievementBean> tapAchievementBeanList = new ArrayList<>();
    private static final int Handle_ADD = 1;
    private static final int Handle_FINISH = 2;

    public void addData(List<TapAchievementBean> data){
        if(data !=null){
            tapAchievementBeanList.clear();
            tapAchievementBeanList.addAll(data);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_archieve, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(tapAchievementBeanList.get(position) , position);

    }

    @Override
    public int getItemCount() {
        return tapAchievementBeanList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView archieve_name;
        private TextView archieve_id;
        private TextView archieve_step;
        private Button add_step;
        private Button finish_archieve;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            archieve_name = itemView.findViewById(R.id.archieve_name);
            archieve_id = itemView.findViewById(R.id.archieve_id);
            archieve_step = itemView.findViewById(R.id.archieve_step);
            add_step = itemView.findViewById(R.id.add_step);
            finish_archieve = itemView.findViewById(R.id.finish_archieve);

        }

        public void setData(TapAchievementBean tapAchievementBean, int position) {
            archieve_name.setText(tapAchievementBean.getTitle().toString());
            archieve_id.setText(tapAchievementBean.getDisplayId().toString());
            archieve_step.setText(tapAchievementBean.getStep()+"步");
            add_step.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(tapAchievementBean, position, Handle_ADD);
                }
            });
            finish_archieve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(tapAchievementBean, position, Handle_FINISH);

                }
            });

        }
    }

    private OnItemClickListener mOnItemClickListener;


    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;

    }

    public interface OnItemClickListener{

        void onClick(TapAchievementBean tapAchievementBean, int position, int handleType);

    }

}
