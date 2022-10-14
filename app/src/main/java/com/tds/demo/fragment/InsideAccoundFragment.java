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
import com.tds.demo.R;
import com.tds.demo.until.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
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
            default:
                break;
        }
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
