package com.tds.demo.fragment.pay;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tapsdk.payment.entities.SkuDetails;
import com.tds.demo.R;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.LCRanking;

/**
 * 2022/10/19
 * Describe：排行榜的 Adapter
 */
public class PayAdapter extends RecyclerView.Adapter<PayAdapter.MyViewHolder>{

    private List<SkuDetails> skuDetailsList = new ArrayList<>();

    public void addData(List<SkuDetails> data){
        if(data !=null){
            skuDetailsList.clear();
            skuDetailsList.addAll(data);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skudetails, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(skuDetailsList.get(position) , position);

    }

    @Override
    public int getItemCount() {
        return skuDetailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView sku_id;
        private TextView goodsName;
        private TextView goodsPrice;
        private Button startPay;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sku_id = itemView.findViewById(R.id.sku_id);
            goodsName = itemView.findViewById(R.id.goodsName);
            goodsPrice = itemView.findViewById(R.id.goodsPrice);
            startPay = itemView.findViewById(R.id.startPay);

        }

        public void setData(SkuDetails skuDetails, int position) {
            sku_id.setText(skuDetails.goodsOpenId);
            goodsName.setText(skuDetails.goodsConfig.goodsName);
            goodsPrice.setText(skuDetails.goodsPrice.goodsPriceAmount+" 元");

            startPay.setOnClickListener(v -> {
                mOnItemClickListener.onClick(skuDetails, position);
            });
        }
    }

    private OnItemClickListener mOnItemClickListener;


    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;

    }

    public interface OnItemClickListener{

        void onClick(SkuDetails skuDetails, int position);

    }

}
