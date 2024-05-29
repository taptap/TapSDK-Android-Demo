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
import com.taptap.pay.sdk.library.TapLicenseHelper;
import com.tds.demo.R;
import com.tds.demo.until.ToastUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 2023-10-11
 * Describe：
 */
public class DLCFragment extends Fragment implements View.OnClickListener{
    private static DLCFragment dlcFragment = null;
    private static final String TAG = "DLCFragment";

    @BindView(R.id.close_button)
    ImageButton closeButton;
    @BindView(R.id.intro_button)
    Button intro_button;

    @BindView(R.id.search)
    Button search;
    @BindView(R.id.pay_dlc)
    Button pay_dlc;


    private String[] skuIds = {"101"};

    public DLCFragment() {
    }

    public static final DLCFragment getInstance() {
        if (dlcFragment == null) {
            dlcFragment = new DLCFragment();
        }
        return dlcFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.layout_dlc_fragment, container, false);
        ButterKnife.bind(this, view);
        closeButton.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        pay_dlc.setOnClickListener(this);
        search.setOnClickListener(this);


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
                Log.e(TAG, "onOrderCallBack: "+ s + i );
                ToastUtil.showCus("购买商品 ID："+ s, ToastUtil.Type.POINT);
            }
        });

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(DLCFragment.getInstance()).commit();
                break;
            case R.id.intro_button:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.cn/docs/sdk/dlc/features/"), null)
                        .addToBackStack("webViewFragment")
                        .commit();
                break;
            case R.id.pay_dlc:
                TapLicenseHelper.purchaseDLC(getActivity(), skuIds[0]);
                break;

            case R.id.search:
                TapLicenseHelper.queryDLC(getActivity(), skuIds);

                break;

            default:
                break;
        }
    }



}


