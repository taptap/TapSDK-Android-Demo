package com.tds.demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tapsdk.bootstrap.Callback;
import com.tapsdk.bootstrap.account.TDSUser;
import com.tapsdk.bootstrap.exceptions.TapError;
import com.tapsdk.lc.LCUser;
import com.taptap.sdk.AccessToken;
import com.taptap.sdk.Profile;
import com.taptap.sdk.TapLoginHelper;
import com.taptap.sdk.net.Api;
import com.tds.demo.R;
import com.tds.demo.until.FormatJson;
import com.tds.demo.until.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 2022-10-11
 * Describe：Tap 登录
 */
public class LoginFragment extends Fragment implements View.OnClickListener{
    private static LoginFragment loginFragment = null;
    private static final String TAG = "LoginFragment";
    @BindView(R.id.close_button)
    ImageButton closeButton;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.tap_login)
    Button tap_login;
    @BindView(R.id.logout)
    Button logout;
    @BindView(R.id.userinfo_button)
    Button userinfo_button;
    @BindView(R.id.user_info)
    TextView user_info;
    @BindView(R.id.refresh_token_button)
    Button refresh_token_button;
    @BindView(R.id.third_bind)
    Button third_bind;


    public LoginFragment() {
    }

    public static final LoginFragment getInstance() {
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
        }
        return loginFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.layout_login_fragment, container, false);
        ButterKnife.bind(this, view) ;

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        closeButton.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        tap_login.setOnClickListener(this);
        userinfo_button.setOnClickListener(this);
        logout.setOnClickListener(this);
        refresh_token_button.setOnClickListener(this);
        third_bind.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(LoginFragment.getInstance()).commit();
                break;
            case R.id.intro_button:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.com/docs/sdk/taptap-login/features/"), null)
                        .addToBackStack("webViewFragment")
                        .commit();
                break;
            case R.id.tap_login:
                tapLogin();
                break;
            case R.id.userinfo_button:
                getUserInfo();
                break;
            case R.id.logout:
                tapLogout();
                break;
            case R.id.refresh_token_button:
                refreshSessionToken();
                break;
            case R.id.third_bind:
                thirdBind();
                break;
            default:
                break;
        }
    }


    /**
     * 如果没有安装 TapTap 客户端则会调用网页授权
     * 静默登录可以帮助用户跳过登录的流程，通常用于用户下一次启动游戏时，仍需之前登录状态的场景。
     * */
    public void tapLogin() {

//      String[] premission = new String[]{"public_profile","user_friends"};    // 开启互关好友接口权限时使用
        String[] premission = new String[]{"public_profile"};
        // 检查登录状态
        if (null == TDSUser.currentUser()) {
            // 未登录
            TDSUser.loginWithTapTap(getActivity(), new Callback<TDSUser>() {
                @Override
                public void onSuccess(TDSUser resultUser) {
                    // 此处也可以获取用户信息
                    Log.e("TAG", "Login onSuccess: "+ resultUser.toJSONInfo() );
                    Profile profile = TapLoginHelper.getCurrentProfile();
                    Log.e(TAG, "onSuccess===》userName: "+ profile );
                    Log.e(TAG, "Access Token ====>: "+ TapLoginHelper.getCurrentAccessToken().access_token);
                    Log.e(TAG, "mac_key ====>: "+ TapLoginHelper.getCurrentAccessToken().mac_key);


                }
                @Override
                public void onFail(TapError error) {
                    ToastUtil.showCus(error.getMessage(), ToastUtil.Type.ERROR );
                    Log.e(TAG, "Login onFail: "+error.toJSON());
                }
            }, premission);
        } else {
            Log.e(TAG, "Access Token ====>: "+ TapLoginHelper.getCurrentAccessToken().access_token);
            Log.e(TAG, "mac_key ====>: "+ TapLoginHelper.getCurrentAccessToken().mac_key);


            Log.e(TAG, "getObjectId: "+ TDSUser.currentUser().getObjectId());

            // 已登录，进入游戏
            ToastUtil.showCus("您已登录！", ToastUtil.Type.POINT );
        }
    }
    /**
     * 重置 Session token
     * */
    private void refreshSessionToken() {
        if(TDSUser.currentUser() != null){
            TDSUser.currentUser().refreshSessionTokenInBackground().subscribe(new Observer<Boolean>() {
                @Override
                public void onSubscribe(Disposable d) {
                }
                @Override
                public void onNext(Boolean aBoolean) {
                    if (aBoolean){
                        ToastUtil.showCus("SessionToken 重置成功", ToastUtil.Type.SUCCEED );
                    }
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
    }

    /**
     * 获取用户信息
     * */
    private void getUserInfo() {

       AccessToken token =  TapLoginHelper.getCurrentAccessToken();
        Log.e(TAG, "getUserInfo: ===="+ token.toString() );


        Profile profile = TapLoginHelper.getCurrentProfile();
        if (null != profile){
            user_info.setText(FormatJson.format(profile.toJsonString()));
        }


        // 获取实时更新的用户信息
        TapLoginHelper.fetchProfileForCurrentAccessToken(new Api.ApiCallback<Profile>() {
            @Override
            public void onSuccess(Profile data) {
                Log.e(TAG, "获取实时更新的用户信息: =====>"+ data.toString() );
            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "获取实时更新的用户信息失败: =====》"+ error.toString() );

            }
        });



    }
    // 退出登录
    private void tapLogout() {

        TDSUser.logOut();
        ToastUtil.showCus("退出登录！", ToastUtil.Type.SUCCEED );

    }

    /**
     * 第三方登录账号与 TapTap 登录账号绑定
     * */
    private void thirdBind() {

        Map<String, Object> thirdPartyData = new HashMap<String, Object>();
        thirdPartyData.put("expires_in", 7200);
        thirdPartyData.put("openid", "my_OPENID");
        thirdPartyData.put("access_token", "oitc7ischkqyjjd4buyg5s5mz");

        TDSUser.getCurrentUser().associateWithAuthData(thirdPartyData, "wechat").subscribe(new Observer<LCUser>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LCUser lcUser) {
                Log.e(TAG, "绑定成功: "+ lcUser);
                Log.e(TAG, "onNext==: "+ TDSUser.getCurrentUser() );
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "绑定失败: "+ e.getMessage());

            }

            @Override
            public void onComplete() {

            }
        });

    }

}


