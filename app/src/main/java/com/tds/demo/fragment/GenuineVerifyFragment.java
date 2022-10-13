package com.tds.demo.fragment;

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


import com.taptap.pay.sdk.library.TapLicenseCallback;
import com.taptap.pay.sdk.library.TapLicenseHelper;
import com.tds.demo.R;

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
        // 默认情况下 SDK 会弹出不可由玩家手动取消的弹窗来避免未授权玩家进入游戏，如果需要回调来触发流程，请添加如下代码
        TapLicenseHelper.setLicenseCallback(new TapLicenseCallback() {
            @Override
            public void onLicenseSuccess() {
                Log.e("TAG", "授权成功的回调！" );
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
    }

    @Override
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
            default:
                break;
        }

    }
}
