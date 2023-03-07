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

import com.tapsdk.bootstrap.account.TDSUser;
import com.tds.demo.R;
import com.tds.demo.until.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 2022/10/14
 * Describe：内建账户
 *
 * 从 TapSDK 3.0 开始，我们提供了一个内建账户系统供游戏使用：开发者可以直接用 TapTap OAuth 授权的结果生
 * 成一个游戏内的账号（TDSUser），同时我们也支持将更多第三方认证登录的结果绑定到该账号上来。
 * TapSDK 提供的游戏内好友、成就等服务和功能，也都基于这一账户系统。
 */
public class InsideAccoundFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.close_button)
    ImageButton close_button;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.anonym_login)
    Button anonym_login;
    @BindView(R.id.set_current_user)
    Button set_current_user;
    @BindView(R.id.set_other_attribute)
    Button set_other_attribute;
    @BindView(R.id.search_user)
    Button search_user;
    @BindView(R.id.third_login)
    Button third_login;






    private static InsideAccoundFragment insideAccoundFragment = null;

    public InsideAccoundFragment() {

    }

    public static final InsideAccoundFragment getInstance() {
        if (insideAccoundFragment == null) {
            insideAccoundFragment = new InsideAccoundFragment();
        }
        return insideAccoundFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.layout_inside_accound_fragment, container, false);
        ButterKnife.bind(this, view);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        close_button.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        anonym_login.setOnClickListener(this);
        set_current_user.setOnClickListener(this);
        set_other_attribute.setOnClickListener(this);
        search_user.setOnClickListener(this);
        third_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.close_button:
                getParentFragmentManager().beginTransaction().remove(InsideAccoundFragment.getInstance()).commit();
                break;
            case R.id.intro_button:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.com/docs/sdk/authentication/features/"), null)
                        .addToBackStack("webViewFragment")
                        .commit();
                break;
            case R.id.anonym_login:
                anonumLogin();
                break;
            case R.id.set_current_user:
                setCurrentUser();
                TDSUser.getCurrentUser().isAuthenticated();
            case R.id.set_other_attribute:
                setOtherAttribute();
                break;
            case R.id.search_user:
                searchUser();
                break;
            case R.id.third_login:
                thirdLogin();
                break;

            default:
                break;
        }
    }


    /**
     * 使用第三方平台登录
     * 需要先在开发着平台配置第三方平台的appId、appkey等信息
     * 然后根据第三方平台登录 SDK 的集成要求进行接入
     * 获取到第三方授权返回的数据后，调用 loginWithAuthData() 完成玩家账户的登录
     *
     * 以下是以 微信 登录为了例
     * */
    private void thirdLogin() {
        Map<String, Object> thirdPartyData = new HashMap<String, Object>();
        // 必须参数
        thirdPartyData.put("expires_in", 7200);
        thirdPartyData.put("openid", "调用微信授权返回的 openid 的值");
        thirdPartyData.put("access_token", "调用微信授权返回的 ACCESS_TOKEN 的值");
        // 可选参数
        thirdPartyData.put("refresh_token", "调用微信授权返回的 refresh_token 的值");
        thirdPartyData.put("scope", "调用微信授权返回的 scope 的值");
        TDSUser.loginWithAuthData(TDSUser.class, thirdPartyData, "weixin").subscribe(new Observer<TDSUser>() {
            public void onSubscribe(Disposable disposable) {
            }
            public void onNext(TDSUser user) {
                System.out.println("成功登录");
                ToastUtil.showCus("登录成功", ToastUtil.Type.SUCCEED);
            }
            public void onError(Throwable throwable) {
                ToastUtil.showCus("登录发生错误："+throwable.getMessage(), ToastUtil.Type.ERROR);

            }
            public void onComplete() {
            }
        });
    }

    /**
     * 查询用户
     * 查询有多少玩家是 1 月 1 日生日的：
     * */
    private void searchUser() {

        LCQuery<TDSUser> userQuery = TDSUser.getQuery(TDSUser.class);
        userQuery.whereEqualTo("birthday", "01-01");
        userQuery.countInBackground().subscribe(new Observer<Integer>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(Integer count) {
                ToastUtil.showCus("生日是1月1日的玩家有："+count+"位", ToastUtil.Type.POINT);
            }
            public void onError(Throwable throwable) {
                ToastUtil.showCus("查询失败："+throwable.getMessage(), ToastUtil.Type.POINT);

            }
            public void onComplete() {}
        });
    }

    /**
     * 设置用户其他属性
     * */
    private void setOtherAttribute() {
        TDSUser currentUser = TDSUser.currentUser();  // 获取当前登录的账户实例
        currentUser.put("nickname","Trackk_New");
        currentUser.put("birthday", "01-01");
        currentUser.put("age", "18");
        currentUser.saveInBackground().subscribe(new Observer<LCObject>() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {

            }

            @Override
            public void onNext(@NotNull LCObject lcObject) {
                // 保存成功，currentUser 的属性得到更新
                TDSUser tdsUser = (TDSUser) lcObject;
                ToastUtil.showCus("用户昵称属性："+tdsUser.getServerData().get("nickname").toString()+"设置成功", ToastUtil.Type.SUCCEED);
            }

            @Override
            public void onError(@NotNull Throwable e) {
                ToastUtil.showCus(e.getMessage(), ToastUtil.Type.ERROR);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 设置当前用户
     * 使用 session token 登录一个用户（云端会验证 session token 是否有效）：
     * */
    private void setCurrentUser() {

        TDSUser.becomeWithSessionTokenInBackground("wavcs62lixesisz365rhp9h9z").subscribe(new Observer<TDSUser>() {
            public void onSubscribe(Disposable disposable) {}
            public void onNext(TDSUser user) {
                // 修改 currentUser
                TDSUser.changeCurrentUser(user, true);
                ToastUtil.showCus("设置当前用户成功", ToastUtil.Type.SUCCEED);
            }
            public void onError(Throwable throwable) {
                // session token 无效
                ToastUtil.showCus("session token 无效", ToastUtil.Type.ERROR);
            }
            public void onComplete() {}
        });


    }

    /**
     * 游客登录
     * */
    private void anonumLogin() {
        TDSUser.logInAnonymously().subscribe(new Observer<TDSUser>() {
            @Override
            public void onSubscribe(Disposable disposable) {

            }

            @Override
            public void onNext(TDSUser resultUser) {
                // 登录成功，得到一个账户实例
                String userId = resultUser.getObjectId();
                ToastUtil.showCus(userId+"登录成功", ToastUtil.Type.SUCCEED );
                Log.e("TAG", "游客登录返回的结果: "+ resultUser.toJSONInfo());
            }

            @Override
            public void onError(Throwable throwable) {
                ToastUtil.showCus(throwable.getMessage(), ToastUtil.Type.ERROR );

            }

            @Override
            public void onComplete() {
            }
        });
    }
}
