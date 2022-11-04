package com.tds.demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tapsdk.billboard.Callback;
import com.tapsdk.billboard.CustomLinkListener;
import com.tapsdk.billboard.TapBillboard;
import com.tapsdk.billboard.UserInteraction;
import com.tapsdk.billboard.exceptions.TapBillboardException;
import com.tapsdk.bootstrap.TapBootstrap;
import com.tds.common.entities.Pair;
import com.tds.common.entities.TapBillboardConfig;
import com.tds.common.entities.TapConfig;
import com.tds.common.models.TapRegionType;
import com.tds.demo.MainActivity;
import com.tds.demo.R;
import com.tds.demo.data.SDKInfoData;
import com.tds.demo.until.ToastUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 2022/11/03
 * Describe：公告系统
 *
 */
public class BillboardFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.close_button)
    ImageButton close_button;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.open_billboard)
    Button open_billboard;
    @BindView(R.id.regist_listener)
    Button regist_listener;
    @BindView(R.id.cancel_listener)
    Button cancel_listener;



    private CustomLinkListener customLinkListenernew;


    private static BillboardFragment billboardFragment = null;

    public BillboardFragment() {

    }

    public static final BillboardFragment getInstance() {
        if (billboardFragment == null) {
            billboardFragment = new BillboardFragment();
        }
        return billboardFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.layout_billboard_fragment, container, false);
        ButterKnife.bind(this, view);
        Set<Pair<String, String>> dimensionSet = new HashSet<>();
        dimensionSet.addAll(Arrays.asList(Pair.create("location", "CN"), Pair.create("platform", "TapTap")));
        String billboardServerUrl = "https://tdsdemo.weijiash.cn"; // 开发者中心 > 你的游戏 > 游戏服务 > 应用配置 > 域名配置 > 公告

        TapBillboardConfig billboardCnConfig = new TapBillboardConfig.Builder()
                .withDimensionSet(dimensionSet)    // 可选
                .withServerUrl(billboardServerUrl) // 必须, 公告的自定义域名
                .withTemplate("navigate") // 可选, 默认是 navigate
                .build();


        TapConfig tdsConfig = new TapConfig.Builder()
                .withAppContext(getActivity())
                .withClientId(SDKInfoData.SDK_CLIENT_ID)
                .withClientToken(SDKInfoData.SDK_CLINT_TOKEN)
                .withServerUrl(SDKInfoData.SDK_SERVER_URL)
                .withBillboardConfig(billboardCnConfig) // 使用公告系统时就必须加入
                .withRegionType(TapRegionType.CN)
                .build();

        TapBillboard.init(tdsConfig);



        return view;

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        close_button.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        open_billboard.setOnClickListener(this);
        regist_listener.setOnClickListener(this);
        cancel_listener.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(BillboardFragment.getInstance()).commit();
                break;
            case R.id.intro_button:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.com/docs/sdk/billboard/features/"), null)
                        .addToBackStack("webViewFragment")
                        .commit();
                break;
            case R.id.open_billboard:
                openBillboard();

                break;
            case R.id.regist_listener:
                registListener();
                break;
            case R.id.cancel_listener:
                cancelListener();
                break;
            default:
                break;
        }
    }

    /**
     * 取消监听
     * */
    private void cancelListener() {
        // 已注册的回调对象需要游戏保存，取消注册的时候要把对象传给 SDK
        TapBillboard.unRegisterCustomLinkListener(customLinkListenernew);

    }

    /**
     * 注册监听
     * */
    private void registListener() {
         customLinkListenernew = new CustomLinkListener() {
            @Override
            public void onCustomUrlClick(String url) {
                // 这里返回的 url 地址和游戏在公告系统内配置的地址是一致的
                // 注意如果有 UI 操作需要切换到 Android 主线程处理
                ToastUtil.showCus("配置地址："+ url, ToastUtil.Type.SUCCEED);
            }
        };

        TapBillboard.registerCustomLinkListener(customLinkListenernew);
    }



    /**
     * 打开公告
     *
     * */
    private void openBillboard() {

        TapBillboard.openPanel(this.getActivity(), new Callback<Void>() {
            @Override
            public void onError(TapBillboardException tapBillboardException) {
                ToastUtil.showCus(tapBillboardException.message, ToastUtil.Type.ERROR);
            }

            @Override
            public void onSuccess(Void result) {
                // 打开公告成功

            }
        },new UserInteraction() {
            @Override
            public void onClose() {
                // 公告已关闭，如果有 UI 更新，请切换到 Android 主线程
                ToastUtil.showCus("公告关闭", ToastUtil.Type.SUCCEED);

            }
        });
    }


}
