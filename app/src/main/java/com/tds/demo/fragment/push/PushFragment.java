package com.tds.demo.fragment.push;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tapsdk.antiaddictionui.AntiAddictionUIKit;
import com.tds.demo.App;
import com.tds.demo.MainActivity;
import com.tds.demo.R;
import com.tds.demo.fragment.AntiaddictionFragment;
import com.tds.demo.fragment.WebViewFragment;
import com.tds.demo.until.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.LCInstallation;
import cn.leancloud.LCPush;
import cn.leancloud.LCQuery;
import cn.leancloud.json.JSONObject;
import cn.leancloud.push.PushService;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 2022/11/8
 * Describe：推送
 */
public class PushFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "PushFragment==》";

    private static PushFragment pushFragment = null;

    @BindView(R.id.close_button)
    ImageButton closeButton;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.device_push)
    Button device_push;
    @BindView(R.id.all_push)
    Button all_push;
    @BindView(R.id.installation_id)
    EditText installation_id;
    @BindView(R.id.content)
    EditText content;




    public PushFragment() {

    }

    public static final PushFragment getInstance() {
        if (pushFragment == null) {
            pushFragment = new PushFragment();
        }
        return pushFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.layout_push_fragment, container, false);
        ButterKnife.bind(this, view);

        startPushService();

        return view;
    }

    /**
     * 开启推送服务
     * */
    private void startPushService() {

        PushService.setDefaultPushCallback(getActivity(), MainActivity.class);

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        installation_id.setText(App.getInstallationId());

        closeButton.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        all_push.setOnClickListener(this);
        device_push.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(PushFragment.getInstance()).commit();
                break;
            case R.id.intro_button:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.com/docs/sdk/push/features/"), null)
                        .addToBackStack("webViewFragment")
                        .commit();
                break;
            case R.id.all_push:
                allPush();
                break;

            case R.id.device_push:
                devicePush();
                break;
            default:
                break;


        }


    }

    /**
     * 全量推送
     * */
    private void allPush() {
        if (content.getText().toString().isEmpty()){
            ToastUtil.showCus("通知内容不能为空", ToastUtil.Type.POINT);
            return;
        }

        LCPush push = new LCPush();
        Map<String, Object> pushData = new HashMap<String, Object>();
        pushData.put("alert",content.getText().toString());
        push.setPushToAndroid(true);
        push.setData(pushData);
        push.sendInBackground().subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject jsonObject) {
                ToastUtil.showCus(jsonObject.toJSONString(), ToastUtil.Type.SUCCEED);
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.showCus(e.getMessage(), ToastUtil.Type.ERROR);
            }

            @Override
            public void onComplete() {

            }
        });
    }


    /**
     * 针对设备推送
     * */
    private void devicePush() {
        if (content.getText().toString().isEmpty()){
            ToastUtil.showCus("通知内容不能为空", ToastUtil.Type.POINT);
            return;
        }
        if (installation_id.getText().toString().isEmpty()){
            ToastUtil.showCus("请输入设备 installationId", ToastUtil.Type.POINT);
            return;
        }

        LCQuery pushQuery = LCInstallation.getQuery();
        pushQuery.whereEqualTo("installationId", installation_id.getText().toString());

        LCPush push = new LCPush();
        push.setQuery(pushQuery);
        push.setMessage(content.getText().toString());
        push.setPushToAndroid(true);
        push.sendInBackground().subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(JSONObject jsonObject) {

                ToastUtil.showCus(jsonObject.toJSONString(), ToastUtil.Type.SUCCEED );
            }
            @Override
            public void onError(Throwable e) {
                ToastUtil.showCus(e.getMessage(), ToastUtil.Type.ERROR );
            }
            @Override
            public void onComplete() {
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView: " );
        getParentFragmentManager().popBackStack("pushFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }
}
