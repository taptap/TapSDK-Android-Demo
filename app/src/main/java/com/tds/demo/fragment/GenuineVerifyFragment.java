package com.tds.demo.fragment;

import static com.tapsdk.bootstrap.constants.Constants.ERROR_CODE_UNDEFINED;
import static com.taptap.pay.sdk.library.DLCManager.QUERY_RESULT_ERR;
import static com.taptap.pay.sdk.library.DLCManager.QUERY_RESULT_NOT_INSTALL_TAPTAP;
import static com.taptap.pay.sdk.library.DLCManager.QUERY_RESULT_OK;

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


import com.taptap.pay.sdk.library.DLCManager;
import com.taptap.pay.sdk.library.TapLicenseCallback;
import com.taptap.pay.sdk.library.TapLicenseHelper;
import com.tds.demo.R;
import com.tds.demo.until.ToastUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 2022/10/13
 * Describe：付费下载和正版验证
 */
public class GenuineVerifyFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.close_button)
    ImageButton close_button;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.check_pay)
    Button check_pay;
    @BindView(R.id.search)
    Button search;
    @BindView(R.id.pay_dlc)
    Button pay_dlc;
    @BindView(R.id.test_environment)
    Button test_environment;

    private String[] skuIds = {"175"};
    private boolean isOpenTest = false;


    private static GenuineVerifyFragment genuineVerifyFragment = null;

    public GenuineVerifyFragment() {

    }

    public static final GenuineVerifyFragment getInstance() {
        if (genuineVerifyFragment == null) {
            genuineVerifyFragment = new GenuineVerifyFragment();
        }
        return genuineVerifyFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.genuine_verify_fragment, container, false);
        ButterKnife.bind(this, view);


        TapLicenseHelper.setLicenseCallback(new TapLicenseCallback() {
            @Override
            public void onLicenseSuccess() {
                ToastUtil.showToast("授权成功！");
            }
        });


        TapLicenseHelper.setDLCCallback(new DLCManager.InventoryCallback() {
            @Override
            public boolean onQueryCallBack(int i, HashMap<String, Integer> queryList) {
                // 查询回调
                if(i == QUERY_RESULT_OK	){
                    ToastUtil.showCus("查询成功："+ queryList.toString(), ToastUtil.Type.SUCCEED);
                } else if (i == QUERY_RESULT_NOT_INSTALL_TAPTAP) {
                    ToastUtil.showCus("设备未安装 TapTap 客户端", ToastUtil.Type.POINT);
                }else if (i == QUERY_RESULT_ERR) {
                    ToastUtil.showCus("查询失败", ToastUtil.Type.ERROR);
                }else if (i == ERROR_CODE_UNDEFINED	) {
                    ToastUtil.showCus("未知错误", ToastUtil.Type.ERROR);
                }
                return false;
            }

            @Override
            public void onOrderCallBack(String s, int i) {
                // 购买回调
                Log.e("TAG", "购买的回调: "+ s +" ==== "+ i );
                ToastUtil.showCus("购买的回调: "+ s +" ==== "+ i, ToastUtil.Type.POINT);
            }
        });


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        close_button.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        check_pay.setOnClickListener(this);
        pay_dlc.setOnClickListener(this);
        search.setOnClickListener(this);
        test_environment.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(GenuineVerifyFragment.getInstance()).commit();
                break;
            case R.id.intro_button:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.com/docs/sdk/copyright-verification/features/"), null)
                        .addToBackStack("webViewFragment")
                        .commit();
                break;
            case R.id.check_pay:
                // 检查是否付费
                TapLicenseHelper.check(getActivity());
                break;
            case R.id.search:
                // DLC 查询
                TapLicenseHelper.queryDLC(getActivity(), skuIds);
                break;
            case R.id.pay_dlc:
                // DLC 购买
                TapLicenseHelper.purchaseDLC(getActivity(), skuIds[0]);
                break;
            case R.id.test_environment:
                if (isOpenTest){
                    ToastUtil.showToast("关闭测试环境！");
                }else{
                    ToastUtil.showToast("开启测试环境！");
                }
                isOpenTest = !isOpenTest;
                TapLicenseHelper.setTestEnvironment(isOpenTest, getActivity());


                break;
            default:
                break;
        }

    }
}
