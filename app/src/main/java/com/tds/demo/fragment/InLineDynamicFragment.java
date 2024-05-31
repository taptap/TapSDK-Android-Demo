package com.tds.demo.fragment;

import static com.tapsdk.moment.TapMoment.CALLBACK_CODE_GET_NOTICE_FAIL;
import static com.tapsdk.moment.TapMoment.CALLBACK_CODE_GET_NOTICE_SUCCESS;
import static com.tapsdk.moment.TapMoment.CALLBACK_CODE_LOGIN_SUCCESS;
import static com.tapsdk.moment.TapMoment.CALLBACK_CODE_MOMENT_APPEAR;
import static com.tapsdk.moment.TapMoment.CALLBACK_CODE_MOMENT_DISAPPEAR;
import static com.tapsdk.moment.TapMoment.CALLBACK_CODE_PUBLISH_CANCEL;
import static com.tapsdk.moment.TapMoment.CALLBACK_CODE_PUBLISH_FAIL;
import static com.tapsdk.moment.TapMoment.CALLBACK_CODE_PUBLISH_SUCCESS;
import static com.tapsdk.moment.TapMoment.CALLBACK_CODE_SCENE_EVENT;

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

import com.tapsdk.moment.TapMoment;
import com.tds.demo.R;
import com.tds.demo.until.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 2022-10-11
 * Describe：内嵌动态
 */
public class InLineDynamicFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.close_button)
    ImageButton imageButton;
    @BindView(R.id.show_dynamic_page)
    Button show_dynamic_page;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.new_notice)
    Button new_notice;
    @BindView(R.id.scene_entry)
    Button scene_entry;
    @BindView(R.id.close_dynamic_page)
    Button close_dynamic_page;
    @BindView(R.id.onekey_send)
    Button onekey_send;




    private static InLineDynamicFragment inLineDynamicFragment = null;

    public InLineDynamicFragment() {

    }

    public static final InLineDynamicFragment getInstance() {
        if (inLineDynamicFragment == null) {
            inLineDynamicFragment = new InLineDynamicFragment();
        }
        return inLineDynamicFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.layout_dynamic_fragment, container, false);
        ButterKnife.bind(this, view);

        // 设置回调方法
        TapMoment.setCallback(new TapMoment.TapMomentCallback() {
            @Override
            public void onCallback(int code, String msg) {
                switch(code){
                    case CALLBACK_CODE_PUBLISH_SUCCESS:
                        ToastUtil.showCus("动态发布成功", ToastUtil.Type.SUCCEED );
                        break;
                    case CALLBACK_CODE_PUBLISH_FAIL	:
                        ToastUtil.showCus("动态发布失败", ToastUtil.Type.SUCCEED );

                        break;
                    case CALLBACK_CODE_PUBLISH_CANCEL:
                        ToastUtil.showCus("关闭动态发布页面", ToastUtil.Type.SUCCEED );

                        break;
                    case CALLBACK_CODE_GET_NOTICE_SUCCESS:
                        Log.e("TAG", "获取新消息成功: "+msg );
                        ToastUtil.showCus("获取新消息成功", ToastUtil.Type.SUCCEED );

                        break;
                    case CALLBACK_CODE_GET_NOTICE_FAIL:
                        ToastUtil.showCus("获取新消息失败", ToastUtil.Type.SUCCEED );

                        break;
                    case CALLBACK_CODE_MOMENT_APPEAR:
                        ToastUtil.showCus("动态页面打开", ToastUtil.Type.SUCCEED );

                        break;
                    case CALLBACK_CODE_MOMENT_DISAPPEAR:
                        ToastUtil.showCus("动态页面关闭", ToastUtil.Type.SUCCEED );

                        break;
                    case CALLBACK_CODE_LOGIN_SUCCESS:
                        ToastUtil.showCus("动态页面内登录成功", ToastUtil.Type.SUCCEED );

                        break;
                    case CALLBACK_CODE_SCENE_EVENT:
                        ToastUtil.showCus("场景化入口回调", ToastUtil.Type.SUCCEED );
                        break;
                    default:
                        break;

                }
            }
        });
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        show_dynamic_page.setOnClickListener(this);
        imageButton.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        new_notice.setOnClickListener(this);
        scene_entry.setOnClickListener(this);
        close_dynamic_page.setOnClickListener(this);
        onekey_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(InLineDynamicFragment.getInstance()).commit();
                break;
            case R.id.intro_button:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.com/docs/sdk/embedded-moments/features/"), null)
                        .addToBackStack("webViewFragment")
                        .commit();
                break;
            case R.id.show_dynamic_page:
                TapMoment.open(TapMoment.ORIENTATION_PORTRAIT);
                break;
            case R.id.new_notice:
                // 定时调用获取消息通知的接口，有新信息时可以在 TapTap 动态入口显示小红点，提醒玩家查看新动态。
                TapMoment.fetchNotification();
                break;
            case R.id.scene_entry:
                Map<String, String> extras = new HashMap<>();
                // 注意：这里的 key 是固定的，"scene_id"；第二个参数是开发者中心后台创建场景化入口后生成的「入口 ID」
                extras.put("scene_id", "taprl0242181002");
                // 注意：第二个参数固定为 "tap://moment/scene/"
                TapMoment.directlyOpen(TapMoment.ORIENTATION_DEFAULT,"tap://moment/scene/", extras);
                break;
            case R.id.close_dynamic_page:
                TapMoment.closeWithConfirmWindow("提示", "关闭内嵌动态！");
                break;
            case R.id.onekey_send:
                int orientation = TapMoment.ORIENTATION_PORTRAIT;
                String content = "动态描述：Tds demo 开发中";
                String[] imagePaths = new String[]{copyAssetGetFilePath("logo.png")};  // 路径可自己设置
                TapMoment.publish(orientation, imagePaths, content);
                break;
            default:
                break;

        }

    }


    private String copyAssetGetFilePath(String fileName) {
        try {
            File cacheDir = getContext().getCacheDir();
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File outFile = new File(cacheDir, fileName);
            if (!outFile.exists()) {
                boolean res = outFile.createNewFile();
                if (!res) {
                    return null;
                }
            } else {
                if (outFile.length() > 10) {//表示已经写入一次
                    return outFile.getPath();
                }
            }
            InputStream is = getContext().getAssets().open(fileName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
            return outFile.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
