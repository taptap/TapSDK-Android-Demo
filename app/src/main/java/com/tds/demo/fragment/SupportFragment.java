package com.tds.demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tapsdk.bootstrap.account.TDSUser;
import com.tds.common.net.exception.ServerException;
import com.tds.demo.R;
import com.tds.tapsupport.TapSupport;
import com.tds.tapsupport.TapSupportCallback;
import com.tds.tapsupport.TapSupportConfig;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.json.JSONObject;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 2022/10/13
 * Describe：防沉迷-合规认证
 */
public class SupportFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.close_button)
    ImageButton closeButton;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.open)
    Button open;
    @BindView(R.id.authorization)
    Button authorization;

    @BindView(R.id.submit_order)
    Button submit_order;
    @BindView(R.id.order_list)
    Button order_list;
    @BindView(R.id.repository)
    Button repository;
    @BindView(R.id.report)
    Button report;
    @BindView(R.id.close_support)
    Button close_support;

    private static SupportFragment supportFragment = null;

    public SupportFragment() {

    }

    public static final SupportFragment getInstance() {
        if (supportFragment == null) {
            supportFragment = new SupportFragment();
        }
        return supportFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.support_fragment, container, false);
        ButterKnife.bind(this, view);
        closeButton.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        open.setOnClickListener(this);
        authorization.setOnClickListener(this);
        submit_order.setOnClickListener(this);
        order_list.setOnClickListener(this);
        repository.setOnClickListener(this);
        report.setOnClickListener(this);
        close_support.setOnClickListener(this);


        initSupport();
        bindToken();

        return view;
    }

    private void bindToken() {

        if (null == TDSUser.currentUser()) {
            // 未登录
            Toast.makeText(this.getActivity(), "请前往登录页完成登录！", Toast.LENGTH_SHORT).show();
            return;
        }
        TDSUser tdsUser= TDSUser.currentUser();
        TDSUser.retrieveShortTokenInBackground(tdsUser.getSessionToken()).subscribe(new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject jsonObject) {
                String credential = jsonObject.getString("identityToken");
                TapSupport.loginWithTDSCredential(credential, new TapSupport.LoginCallback() {
                    @Override
                    public void onComplete(boolean success, Throwable error) {
                        if (success) {
                            // 登陆成功
                            Log.e("TAG", "TapSupport onComplete: "+"登录成功！" );
                            Toast.makeText(getActivity(), "登录工单系统成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            // 登录失败
                            Log.e("TAG", "login:error:" + error.toString());
                            if (error instanceof ServerException) {
                                try {
                                    org.json.JSONObject errorResponse = new org.json.JSONObject(((ServerException) error).responseBody);
                                    if(errorResponse.getInt("numCode")==9006){
                                        // 登录过期
                                        Log.e("TAG", "登录过期" );
                                    }
                                } catch (JSONException ex) {
                                    // ignore
                                    Log.e("TAG", "JSONException"+ex.toString() );

                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable error) {
                // 请求授权 token 失败
                Log.e("TapSupportActivity", "请求授权 Token 失败！");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void initSupport() {
        TapSupportConfig config = new TapSupportConfig("https://ywj.support.tdspowered.cn", "14", new TapSupportCallback() {
            @Override
            public void onGetUnreadStatusError(Throwable throwable) {
                Log.e("TAG", "onGetUnreadStatusError: "+ throwable.toString());
            }

            @Override
            public void onUnreadStatusChanged(boolean b) {
                Log.e("TAG", "onUnreadStatusChanged: "+ b);
                if(b){
                    Toast.makeText(getActivity(), "工单有新的回复，请查看！", Toast.LENGTH_SHORT).show();
                }

            }
        });
        TapSupport.setConfig(this.getActivity(), config);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(SupportFragment.getInstance()).commit();
                break;
            case R.id.intro_button:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.cn/docs/sdk/tap-support/features/"), null)
                        .addToBackStack("webViewFragment")
                        .commit();
                break;
            case R.id.open:
                openSupport("");
                break;
            case R.id.authorization:
                bindToken();
                break;
            case R.id.submit_order:
                openSupport("/tickets/new?category_id=19");
                break;
            case R.id.order_list:
                openSupport("/tickets");

                break;
            case R.id.repository:
                openSupport("/articles/6");
                break;
            case R.id.close_support:
                closeSupport();
                break;
            case R.id.report:
                submitReport();
                break;
            default:
                break;
        }

    }
//    上报信息
    private void submitReport() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("243", "iOS 15.1"); // 243 是在后台创建的「OS」字段的 ID
        fields.put("244", "Dash"); // 244 是在后台创建的「角色名」字段的 ID

        TapSupport.openSupportView("/tickets", fields);
    }

    //    关闭客服页面
    private void closeSupport() {
        TapSupport.closeSupportView();

    }

    private void openSupport(String path) {
        if(path.isEmpty()){
            TapSupport.openSupportView();
        }else {
            TapSupport.openSupportView(path);
        }

    }


}
