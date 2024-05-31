package com.tds.demo.fragment;

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

import com.tapsdk.antiaddiction.Callback;
import com.tapsdk.antiaddiction.Config;
import com.tapsdk.antiaddiction.constants.Constants;
import com.tapsdk.antiaddiction.entities.response.CheckPayResult;
import com.tapsdk.antiaddiction.entities.response.SubmitPayResult;
import com.tapsdk.antiaddictionui.AntiAddictionUICallback;
import com.tapsdk.antiaddictionui.AntiAddictionUIKit;
import com.taptap.sdk.Profile;
import com.taptap.sdk.TapLoginHelper;
import com.tds.demo.R;
import com.tds.demo.data.SDKInfoData;
import com.tds.demo.until.ToastUtil;

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
    @BindView(R.id.get_time_remaining)
    Button get_time_remaining;
    @BindView(R.id.submit_pay)
    Button submit_pay;
    @BindView(R.id.test_btn)
    Button test_btn;
    @BindView(R.id.upload_amount)
    EditText upload_amount;
    @BindView(R.id.examine_amount)
    EditText examine_amount;







    private static AntiaddictionFragment antiaddictionFragment = null;

    private boolean isOpenTest = false;
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

        // 单独初始化防沉迷 SDk
//        aloneInit();

        // 注册防沉迷的消息监听
        AntiAddictionUIKit.setAntiAddictionCallback(new AntiAddictionUICallback() {
            @Override
            public void onCallback(int code, Map<String, Object> extras) {
                Log.e("TAG", "onCallback:=====  "+code +"   "+extras  );
                String token = AntiAddictionUIKit.currentToken();
                Log.e("TAG", "Token: "+ token );

                if (code == Constants.ANTI_ADDICTION_CALLBACK_CODE.LOGIN_SUCCESS){
                    ToastUtil.showCus("玩家登录后判断当前玩家可以进行游戏", ToastUtil.Type.SUCCEED );

                }else if(code == Constants.ANTI_ADDICTION_CALLBACK_CODE.EXITED){
                    ToastUtil.showCus("退出账号", ToastUtil.Type.SUCCEED );

                }else if(code == Constants.ANTI_ADDICTION_CALLBACK_CODE.SWITCH_ACCOUNT){
                    ToastUtil.showCus("点击切换账号按钮", ToastUtil.Type.SUCCEED );

                }else if(code == Constants.ANTI_ADDICTION_CALLBACK_CODE.PERIOD_RESTRICT){
                    ToastUtil.showCus("未成年玩家当前无法进行游戏", ToastUtil.Type.SUCCEED );

                }else if(code == Constants.ANTI_ADDICTION_CALLBACK_CODE.DURATION_LIMIT	){
                    ToastUtil.showCus("时长限制", ToastUtil.Type.SUCCEED );

                }else if(code == Constants.ANTI_ADDICTION_CALLBACK_CODE.AGE_RESTRICT	){
                    ToastUtil.showCus("当前用户因触发应用设置的年龄限制无法进入游戏", ToastUtil.Type.SUCCEED );

                }else if(code == Constants.ANTI_ADDICTION_CALLBACK_CODE.INVALID_CLIENT_OR_NETWORK_ERROR	){
                    ToastUtil.showCus("数据请求失败，游戏需检查当前设置的应用信息是否正确及判断当前网络连接是否正常", ToastUtil.Type.SUCCEED );

                }else if(code == Constants.ANTI_ADDICTION_CALLBACK_CODE.REAL_NAME_STOP	){
                    ToastUtil.showCus("实名过程中点击了关闭实名窗", ToastUtil.Type.SUCCEED );
                }
            }
        });

        return view;
    }

    private void aloneInit() {
        AntiAddictionUICallback callback = new AntiAddictionUICallback() {
            @Override
            public void onCallback(int code, Map<String, Object> extras) {
                // 防沉迷回调
                Log.e("TAG", "onCallback: "+ code);
            }
        };

        Config config = new Config.Builder()
                .withClientId(SDKInfoData.SDK_CLIENT_ID) // TapTap 开发者中心对应 Client ID
                .showSwitchAccount(false)       // 是否显示切换账号按钮
                .useAgeRange(false)  //是否使用年龄段信息
                .build();
        AntiAddictionUIKit.init(getActivity(), config, callback);
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
        get_time_remaining.setOnClickListener(this);
        submit_pay.setOnClickListener(this);
        test_btn.setOnClickListener(this);


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
          case R.id.get_time_remaining:
              remainingTime();
              break;
          case R.id.submit_pay:
              submitPayMoney();
              break;
          case R.id.test_btn:
               openOrCloseTest();
              break;
          default:
              break;


      }
    }


    // 设置测试环境
    private void openOrCloseTest() {
        if(isOpenTest){
            ToastUtil.showToast("关闭测试模式");
        }else{
            ToastUtil.showToast("开启测试模式");
        }
        isOpenTest = !isOpenTest;
        AntiAddictionUIKit.setTestEnvironment(getActivity(), isOpenTest);

    }

    private void getGamerAge() {
        int ageRange = AntiAddictionUIKit.getAgeRange();
        ToastUtil.showCus("当前年龄段最低年龄为："+ ageRange, ToastUtil.Type.SUCCEED);
    }

    /**
     * 上报未成年玩家的消费金额
     * 建议开发者在服务端上报，服务端上报方式参见 相关 REST API 用法说明。
     * 开发者也可以调用 SDK 提供的接口，当未成年玩家消费成功后，在客户端上报消费金额，在客户端上报的可靠性低于在服务端上报，主要适用于无服务端的单机游戏。
     * */
    private void submitPayMoney() {
        String uploadAmount = upload_amount.getText().toString();
        if(uploadAmount.isEmpty() || convertToLong(uploadAmount) == 0L){
            ToastUtil.showToast("请输入正确的金额");
            return;
        }
        AntiAddictionUIKit.submitPayResult(convertToLong(uploadAmount), new Callback<SubmitPayResult>() {
                    @Override
                    public void onSuccess(SubmitPayResult result) {
                        // 提交成功
                        ToastUtil.showCus("提交成功", ToastUtil.Type.SUCCEED );

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        // 处理异常
                        ToastUtil.showCus("处理异常！", ToastUtil.Type.ERROR );
                    }
                }
        );
    }

    /**
     * 检查消费上限
     * 根据年龄段的不同，未成年玩家的消费金额有不同的上限。 如果启用消费限制功能，开发者需要在未成年玩家消费前检查是否受限，并在成功消费后上报消费金额。
     * 消费金额的单位为 分。
     * */
    private void checkPay() {
        String examineAmount = examine_amount.getText().toString();
        if(examineAmount.isEmpty() || convertToLong(examineAmount) == 0L){
           ToastUtil.showToast("请输入正确的金额");
            return;
        }
        AntiAddictionUIKit.checkPayLimit(getActivity(), convertToLong(examineAmount),
                new com.tapsdk.antiaddictionui.Callback<CheckPayResult>() {
                    @Override
                    public void onSuccess(CheckPayResult result) {
                        // status 为 true 时可以支付，false 则限制消费
                        if (result.status) {
                            ToastUtil.showCus("可以进行充值操作！", ToastUtil.Type.SUCCEED );

                        }else{
                            Log.e("TAG", "消费受限: "+result.description);
                            ToastUtil.showCus("消费受限！", ToastUtil.Type.SUCCEED );

                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("TAG", "onError: "+throwable.getMessage() );
                        // 处理异常
                        ToastUtil.showCus(throwable.getMessage(), ToastUtil.Type.ERROR );

                    }
                }
        );
        }


    private long convertToLong(String amount) {
        if (amount != null && amount.matches("-?\\d+")){
            try {
                return Long.parseLong(amount);
            } catch (NumberFormatException e) {
                return 0L;
            }
        } else {
            ToastUtil.showToast("请输入正确的金额");
            return 0L; // 你可以根据需要选择其他默认值或抛出异常
        }
    }

    /**
    * 获取剩余时长（单位:分钟）
    * */
    private void remainingTime() {
        int remainingTimeInMinutes = AntiAddictionUIKit.getRemainingTimeInMinutes();
        ToastUtil.showCus("剩余时长："+ remainingTimeInMinutes +"分钟", ToastUtil.Type.SUCCEED );

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
        String userIdentifier = "";

        Profile profile = TapLoginHelper.getCurrentProfile();
        if (profile != null) {
            userIdentifier = profile.getUnionid();
            Log.e("TAG", "=== "+userIdentifier  );
        } else {

            ToastUtil.showCus("请登录", ToastUtil.Type.POINT);
        }

//        userIdentifier = System.currentTimeMillis()+"";
        AntiAddictionUIKit.startupWithTapTap(getActivity(), userIdentifier);


    }
}
