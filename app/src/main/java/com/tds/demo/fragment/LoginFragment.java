package com.tds.demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.tapsdk.bootstrap.Callback;
import com.tapsdk.bootstrap.account.TDSUser;
import com.tapsdk.bootstrap.exceptions.TapError;
import com.taptap.sdk.Profile;
import com.taptap.sdk.TapLoginHelper;
import com.taptap.sdk.net.Api;
import com.tds.demo.R;
import com.tds.demo.until.FormatJson;
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
            default:
                break;
        }
    }



    /**
     * 如果没有安装 TapTap 客户端则会调用网页授权
     * */
    public void tapLogin() {

        // 检查登录状态
        if (null == TDSUser.currentUser()) {
            // 未登录
            TDSUser.loginWithTapTap(getActivity(), new Callback<TDSUser>() {
                @Override
                public void onSuccess(TDSUser resultUser) {
                    // 此处也可以获取用户信息
                    Log.e("TAG", "onSuccess: "+ resultUser.toJSONInfo() );
                    Toast.makeText(getActivity(), "恭喜 "+resultUser.getServerData().get("nickname")+" 登录成功！", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFail(TapError error) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }, "public_profile");

        } else {
            // 已登录，进入游戏
            Toast.makeText(getActivity(),"您已登录！", Toast.LENGTH_SHORT ).show();
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
                        Toast.makeText(getActivity(), "SessionToken 重置成功", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onError(Throwable e) {
                    Log.e("TAG", "onError: "+ e.getMessage());
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
        Toast.makeText(getActivity(),"退出登录！", Toast.LENGTH_SHORT ).show();

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
                    Toast.makeText(getActivity(), "该玩家已具有篝火测试资格", Toast.LENGTH_SHORT).show();
                }else {
                    // 该玩家不具备测试资格， 游戏层面进行拦截
                    Toast.makeText(getActivity(), "该玩家不具备篝火测试资格", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onError(Throwable throwable) {
                // 服务端检查出错或者网络异常
                Toast.makeText(getActivity(), "服务端检查出错或者网络异常", Toast.LENGTH_SHORT).show();
            }
        });



    }
}
