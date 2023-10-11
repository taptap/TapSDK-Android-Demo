package com.tds.demo.fragment.pay;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapsdk.payment.Callback;
import com.tapsdk.payment.ListCallback;
import com.tapsdk.payment.PurchaseCallback;
import com.tapsdk.payment.TapPayment;
import com.tapsdk.payment.entities.SkuDetails;
import com.tapsdk.payment.exceptions.TapPaymentException;
import com.tds.demo.R;
import com.tds.demo.fragment.WebViewFragment;
import com.tds.demo.fragment.ranking.RankingAdapter;
import com.tds.demo.until.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 2023-10-11
 * Describe：支付系统
 */
public class PayFragment extends Fragment implements View.OnClickListener{
    private static PayFragment payFragment = null;
    private static final String TAG = "payFragment";
    private PayAdapter payAdapter = null;

    private List<SkuDetails> skuDetailsList = new ArrayList<>();
    private String skuId_01 = "xiaohao001";
    private String skuId_02 = "xiaohao002";
    private String skuId_03 = "feixiaohao001";
    private String skuId_04 = "feixiaohao002";

    @BindView(R.id.close_button)
    ImageButton closeButton;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.single_commodity)
    Button single_commodity;
    @BindView(R.id.multiple_commodity)
    Button multiple_commodity;
    @BindView(R.id.recycle_view)
    RecyclerView recycle_view;



    public PayFragment() {
    }

    public static final PayFragment getInstance() {
        if (payFragment == null) {
            payFragment = new PayFragment();
        }
        return payFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.layout_pay_fragment, container, false);
        ButterKnife.bind(this, view);


        payAdapter = new PayAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle_view.setLayoutManager(linearLayoutManager);
        recycle_view.setAdapter(payAdapter);
        payAdapter.setmOnItemClickListener(new PayAdapter.OnItemClickListener() {
            @Override
            public void onClick(SkuDetails skuDetails, int position) {
                startPay(skuDetails);
            }
        });

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        closeButton.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        single_commodity.setOnClickListener(this);
        multiple_commodity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(PayFragment.getInstance()).commit();
                break;
            case R.id.intro_button:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.cn/docs/tds-payment/"), null)
                        .addToBackStack("webViewFragment")
                        .commit();
                break;
            case R.id.single_commodity:
                searchSingleCommodity();
                break;

            case R.id.multiple_commodity:
                searchMultipleCommodity();
                break;

            default:
                break;
        }
    }


    /**
     * 启动购买流程
     * activity 唤起购买流程的 Activity
     * skuDetails 购买的商品信息
     * roleId 游戏内角色 id
     * serverId 游戏的服务器 id
     * extra 游戏的额外信息 json 格式的字符串 eg."{\"test\":\"test\"}"
     * purchaseCallback
     */
    private void startPay(SkuDetails skuDetails) {

        TapPayment.launchBillingFlow(getActivity()
                , skuDetails
                , "001"
                , "001"
                , "{'test' : 'test'}"
                , new PurchaseCallback() {
                    @Override
                    public void onPurchaseUpdated(int responseCode, String message) {
                        // do some thing
                        if (responseCode == 0){
                            ToastUtil.showCus("购买完成", ToastUtil.Type.SUCCEED);
                        } else if (responseCode == 1) {
                            ToastUtil.showCus("购买异常", ToastUtil.Type.ERROR);
                        } else if (responseCode == 2) {
                            ToastUtil.showCus("用户取消", ToastUtil.Type.POINT);
                        }
                    }
                }
        );
    }

    /**
     * 查询单个商品
     */
    private void searchSingleCommodity() {
        TapPayment.queryProduct(skuId_01, new Callback<SkuDetails>() {
            @Override
            public void onSuccess(SkuDetails skuDetails) {
                if (skuDetails == null) {
                    // not found any product with given skuId
                    ToastUtil.showCus("未查询到商品", ToastUtil.Type.POINT);
                } else {
                    skuDetailsList.clear();
                    skuDetailsList.add(skuDetails);
                    payAdapter.addData(skuDetailsList);
                    ToastUtil.showCus("商品名："+skuDetails.goodsConfig.goodsName+"  价格："+skuDetails.goodsPrice.goodsPriceAmount+"元" , ToastUtil.Type.SUCCEED);
                    Log.e(TAG, "商品详情: "+skuDetails.toString() );
                }
            }

            @Override
            public void onError(TapPaymentException e) {
                Log.e(TAG, "onError: "+e.message.toString() );
            }
        });
    }
    /**
     *
     * 查询多个商品
     */
    private void searchMultipleCommodity() {

        TapPayment.queryProducts(Arrays.asList(skuId_01, skuId_02, skuId_03,skuId_04), new ListCallback<SkuDetails>() {
            @Override
            public void onSuccess(List<SkuDetails> resultList) {
                if (resultList.size() == 0) {
                    ToastUtil.showCus("未查询到商品", ToastUtil.Type.POINT);

                } else {
                    String pointText = "";

                    for (int i = 0; i < resultList.size(); i++) {
                        pointText = pointText+ "商品名："+resultList.get(i).goodsConfig.goodsName+"  价格："+resultList.get(i).goodsPrice.goodsPriceAmount+"元\n";
                    }
                    ToastUtil.showCus(pointText , ToastUtil.Type.SUCCEED);

                    payAdapter.addData(resultList);

                    Log.e(TAG, "商品详情: "+resultList.toString() );

                }
            }

            @Override
            public void onError(TapPaymentException tapPaymentException) {
                Log.e(TAG, "onError: "+tapPaymentException.message.toString() );
            }
        });
    }


}


