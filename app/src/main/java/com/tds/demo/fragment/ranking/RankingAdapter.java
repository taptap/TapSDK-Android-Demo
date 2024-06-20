package com.tds.demo.fragment.ranking;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tapsdk.lc.LCRanking;
import com.tds.demo.R;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 2022/10/19
 * Describe：排行榜的 Adapter
 */
public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.MyViewHolder>{

    private List<LCRanking> lCRankingList = new ArrayList<>();

    public void addData(List<LCRanking> data){
        if(data !=null){
            lCRankingList.clear();
            lCRankingList.addAll(data);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(lCRankingList.get(position) , position);

    }

    @Override
    public int getItemCount() {
        return lCRankingList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView user_id;
        private TextView ranking_value;
        private Button handle_button;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            user_id = itemView.findViewById(R.id.user_id);
            ranking_value = itemView.findViewById(R.id.ranking_value);
            handle_button = itemView.findViewById(R.id.handle_button);


        }

        public void setData(LCRanking lCRanking, int position) {
            try {
                JSONObject jsonObject = new JSONObject(lCRanking.getUser().toJSONString());
                if(jsonObject.has("serverData")){
                    JSONObject serverData = jsonObject.getJSONObject("serverData");
                    if(serverData.has("nickname")){
                        user_id.setText(serverData.getString("nickname"));
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            ranking_value.setText(lCRanking.getStatisticValue()+"");

            handle_button.setOnClickListener(v -> mOnItemClickListener.onClick(lCRanking, position));
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;

    }

    public interface OnItemClickListener{

        void onClick(LCRanking lCRanking, int position);

    }

}
