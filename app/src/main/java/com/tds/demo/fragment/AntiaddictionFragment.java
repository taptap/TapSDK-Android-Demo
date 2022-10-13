package com.tds.demo.fragment;

import android.media.Image;
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

import com.tapsdk.antiaddiction.Callback;
import com.tapsdk.antiaddiction.Config;
import com.tapsdk.antiaddiction.constants.Constants;
import com.tapsdk.antiaddiction.entities.response.CheckPayResult;
import com.tapsdk.antiaddiction.entities.response.SubmitPayResult;
import com.tapsdk.antiaddictionui.AntiAddictionUICallback;
import com.tapsdk.antiaddictionui.AntiAddictionUIKit;
import com.tds.demo.R;
import com.tds.demo.data.SDKInfoData;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 2022/10/13
 * Describe：防沉迷-合规认证
 */
public class AntiaddictionFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.close_button)
    ImageButton close_button;
    @BindView(R.id.intro_button)
    Button intro_button;
    @BindView(R.id.quick_authentication)
    Button quick_authentication;
    @BindView(R.id.logout)
    Button logout;
    @BindView(R.id.age)
    Button age;
    @BindView(R.id.pay_limit)
    Button pay_limit;
    @BindView(R.id.open_game_time)
    Button open_game_time;
    @BindView(R.id.close_game_time)
    Button close_game_time;
    @BindView(R.id.get_time_remaining)
    Button get_time_remaining;
    @BindView(R.id.submit_pay)
    Button submit_pay;



    private static AntiaddictionFragment antiaddictionFragment = null;

    public AntiaddictionFragment() {

    }

    public static final AntiaddictionFragment getInstance() {
        if (antiaddictionFragment == null) {
            antiaddictionFragment = new AntiaddictionFragment();
        }
        return antiaddictionFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.antiaddiction_fragment, container, false);
        ButterKnife.bind(this, view);

        // 实名认证的初始化
        Config config = new Config.Builder()
                .withClientId(SDKInfoData.SDK_CLIENT_ID) // TapTap 开发者中心对应 Client ID
                .enableTapLogin(true)           // 是否启动 TapTap 快速认证
                .showSwitchAccount(false)       // 是否显示切换账号按钮
                .build();

        // 注册防沉迷的消息监听
        AntiAddictionUIKit.init(getActivity(), config, new AntiAddictionUICallback() {
            @Override
            public void onCallback(int code, Map<String, Object> extras) {
                if (code == Constants.ANTI_ADDICTION_CALLBACK_CODE.LOGIN_SUCCESS){
                    Toast.makeText(getActivity(), "玩家登录后判断当前玩家可以进行游戏", Toast.LENGTH_SHORT).show();
                }else if(code == Constants.ANTI_ADDICTION_CALLBACK_CODE.EXITED){
                    Toast.makeText(getActivity(), "退出账号", Toast.LENGTH_SHORT).show();
                }else if(code == Constants.ANTI_ADDICTION_CALLBACK_CODE.SWITCH_ACCOUNT){
                    Toast.makeText(getActivity(), "点击切换账号按钮", Toast.LENGTH_SHORT).show();
                }else if(code == Constants.ANTI_ADDICTION_CALLBACK_CODE.PERIOD_RESTRICT){
                    Toast.makeText(getActivity(), "未成年玩家当前无法进行游戏", Toast.LENGTH_SHORT).show();
                }else if(code == Constants.ANTI_ADDICTION_CALLBACK_CODE.DURATION_LIMIT	){
                    Toast.makeText(getActivity(), "时长限制", Toast.LENGTH_SHORT).show();
                }else if(code == Constants.ANTI_ADDICTION_CALLBACK_CODE.REAL_NAME_STOP	){
                    Toast.makeText(getActivity(), "实名过程中点击了关闭实名窗\n", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        close_button.setOnClickListener(this);
        intro_button.setOnClickListener(this);
        quick_authentication.setOnClickListener(this);
        logout.setOnClickListener(this);
        age.setOnClickListener(this);
        pay_limit.setOnClickListener(this);
        open_game_time.setOnClickListener(this);
        close_game_time.setOnClickListener(this);
        get_time_remaining.setOnClickListener(this);
        submit_pay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()){
          case R.id.close_button:
              getParentFragmentManager().beginTransaction().remove(AntiaddictionFragment.getInstance()).commit();
              break;
          case R.id.intro_button:
              entryDocument();
              break;
          case R.id.quick_authentication:
              setQuick_authentication();
              break;
          case R.id.logout:
              antiaddictionExit();
              break;
          case R.id.age:
              getGamerAge();
              break;
          case R.id.pay_limit:
              checkPay();
              break;
          case R.id.open_game_time:
              AntiAddictionUIKit.enterGame();
              break;
          case R.id.close_game_time:
              AntiAddictionUIKit.leaveGame();
              break;
          case R.id.get_time_remaining:
              remainingTime();
              break;
          case R.id.submit_pay:
              submitPayMoney();
              break;
          default:
              break;


      }
    }

    /**
     * 上报未成年玩家的消费金额
     * 建议开发者在服务端上报，服务端上报方式参见 相关 REST API 用法说明。
     * 开发者也可以调用 SDK 提供的接口，当未成年玩家消费成功后，在客户端上报消费金额，在客户端上报的可靠性低于在服务端上报，主要适用于无服务端的单机游戏。
     * */
    private void submitPayMoney() {
        long amount = 100;
        AntiAddictionUIKit.submitPayResult(amount, new Callback<SubmitPayResult>() {
                    @Override
                    public void onSuccess(SubmitPayResult result) {
                        // 提交成功
                        Toast.makeText(getActivity(), "提交成功！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        // 处理异常
                        Toast.makeText(getActivity(), "处理异常！", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * 检查消费上限
     * 根据年龄段的不同，未成年玩家的消费金额有不同的上限。 如果启用消费限制功能，开发者需要在未成年玩家消费前检查是否受限，并在成功消费后上报消费金额。
     * 消费金额的单位为分。
     * */
    private void checkPay() {
        long amount = 100;  // 单位为 分
        AntiAddictionUIKit.checkPayLimit(getActivity(), amount,
                new com.tapsdk.antiaddictionui.Callback<CheckPayResult>() {
                    @Override
                    public void onSuccess(CheckPayResult result) {
                        // status 为 true 时可以支付，false 则限制消费
                        if (result.status) {
                            Toast.makeText(getActivity(), "可以进行充值操作！", Toast.LENGTH_SHORT).show();
                        }else{
                            Log.e("TAG", "消费受限: "+result.description);
                            Toast.makeText(getActivity(), "消费受限！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        // 处理异常
                    }
                }
        );
    }

    /**
    * 获取剩余时长（单位:分钟）
    * */
    private void remainingTime() {
        int remainingTimeInMinutes = AntiAddictionUIKit.getRemainingTimeInMinutes();
        Toast.makeText(getActivity(), "剩余时长："+ remainingTimeInMinutes +"分钟", Toast.LENGTH_SHORT).show();


    }

    /**
    * 获取玩家年龄
    * ageRange 是一个整数，表示玩家所处年龄段的下限（最低年龄）。 特别地，-1 表示「未知」，说明该用户还未实名。
    * */
    private void getGamerAge() {
        int ageRange = AntiAddictionUIKit.getAgeRange();
        Toast.makeText(getActivity(), "玩家年龄是："+ ageRange, Toast.LENGTH_SHORT).show();
    }


    /**
     *
     * 玩家在游戏内退出账号时调用，重置防沉迷状态。
     * */
    private void antiaddictionExit() {
        AntiAddictionUIKit.exit();

    }

    private void entryDocument() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, WebViewFragment.getInstance("https://developer.taptap.com/docs/sdk/anti-addiction/features/"), null)
                .addToBackStack("webViewFragment")
                .commit();
    }

    /**
    * Tap 快速认证
    * 实名认证是根据唯一标识进行判断是否已实名认证，所以建议该唯一标识和用户唯一绑定
    * */
    private void setQuick_authentication() {
        String userIdentifier = "XXXXXXXXXXXXXXX";
        AntiAddictionUIKit.startup(getActivity(), userIdentifier);
    }
}