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
import com.taptap.sdk.AccessToken;
import com.taptap.sdk.AccountGlobalError;
import com.taptap.sdk.Profile;
import com.taptap.sdk.TapLoginHelper;
import com.taptap.sdk.net.Api;
import com.tds.demo.R;
import com.tds.demo.data.SDKInfoData;
import com.tds.demo.until.FormatJson;
import com.tds.demo.until.ToastUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.LCUser;
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
    @BindView(R.id.test_status)
    Button test_status;
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
        ButterKnife.bind(this, view);

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
        test_status.setOnClickListener(this);
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
            case R.id.test_status:
                testStatus();
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

        // 检查登录状态
        if (null == TDSUser.currentUser()) {
            // 未登录
            TDSUser.loginWithTapTap(getActivity(), new Callback<TDSUser>() {
                @Override
                public void onSuccess(TDSUser resultUser) {
                    // 此处也可以获取用户信息
                    Log.e("TAG", "Login onSuccess: "+ resultUser.toJSONInfo() );
                    ToastUtil.showCus("恭喜 "+resultUser.getServerData().get("nickname")+" 登录成功！", ToastUtil.Type.SUCCEED );
                    resultUser.getObjectId();
                }
                @Override
                public void onFail(TapError error) {
                    ToastUtil.showCus(error.getMessage(), ToastUtil.Type.ERROR );
                    Log.e(TAG, "Login onFail: "+error.code);
                }
            }, "public_profile");

        } else {
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
        Profile profile = TapLoginHelper.getCurrentProfile();
        if (null != profile){
            user_info.setText(FormatJson.format(profile.toJsonString()));
        }

    }
    // 退出登录
    private void tapLogout() {
        TDSUser.logOut();
        ToastUtil.showCus("退出登录！", ToastUtil.Type.SUCCEED );

    }

    /**
     * 测试资格校验
     * 该功能仅用于需要上线「篝火测试服」的游戏，对有登录白名单的用户进行资格校验，防止测试阶段开发包外传被利用
     * Error 信息为网络错误，或者该游戏未开通篝火测试服。
     * */
    private void testStatus() {

        TapLoginHelper.getTestQualification(new Api.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if(aBoolean){
                    // 该玩家已拥有测试资格
                    ToastUtil.showCus("该玩家已具有篝火测试资格", ToastUtil.Type.SUCCEED );
                    Log.e(TAG, "onSuccess: "+"该玩家已具有篝火测试资格" );
                }else {
                    // 该玩家不具备测试资格， 游戏层面进行拦截
                    ToastUtil.showCus("该玩家不具备篝火测试资格", ToastUtil.Type.SUCCEED );
                    Log.e(TAG, "该玩家不具备篝火测试资格" );
                }
            }
            @Override
            public void onError(Throwable throwable) {
                // 服务端检查出错或者网络异常
                ToastUtil.showCus("服务端检查出错或者网络异常", ToastUtil.Type.SUCCEED );
                Log.e(TAG, "onError: "+"服务端检查出错或者网络异常" );

            }
        });




    }


    // 账号密码注册
    private void userAndPasswotd(){
        // 创建实例
        LCUser user = new LCUser();
        user.setUsername("Tom11");
        user.setPassword("cat!@#123");

// 可选
//        user.setEmail("tom@leancloud.rocks");
//        user.setMobilePhoneNumber("+8618200008888");

//        设置其他属性的方法跟 LCObject 一样
//        user.put("gender", "secret");

        user.signUpInBackground().subscribe(new Observer<LCUser>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCUser user) {
                // 注册成功
                Log.e(TAG, "注册成功："+ user.toJSONString());
            }
            public void onError(Throwable throwable) {
                // 注册失败（通常是因为用户名已被使用）
                Log.e(TAG, "注册失败："+ throwable.getLocalizedMessage());

            }
            public void onComplete() {}
        });

    }


    /*
    * 账号密码登录
    * */

    private void userAndPasswotdLogin(){
        LCUser.logIn("Tom11", "cat!@#123").subscribe(new Observer<LCUser>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(LCUser user) {
                // 登录成功
                Log.e(TAG, "登录成功："+ user.toJSONString());

            }
            public void onError(Throwable throwable) {
                // 登录失败（可能是密码错误）
                Log.e(TAG, "登录失败："+ throwable.getLocalizedMessage());

            }
            public void onComplete() {}
        });

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


