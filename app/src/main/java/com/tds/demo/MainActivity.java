package com.tds.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.tapsdk.bootstrap.TapBootstrap;
import com.tapsdk.tapconnect.TapConnect;
import com.tds.common.constants.Constants;
import com.tds.common.entities.Pair;
import com.tds.common.entities.TapBillboardConfig;
import com.tds.common.entities.TapConfig;
import com.tds.common.entities.TapDBConfig;
import com.tds.common.entities.TapPaymentConfig;
import com.tds.common.models.TapRegionType;
import com.tds.demo.data.SDKInfoData;
import com.tds.demo.data.SDKTypeData;
import com.tds.demo.fragment.AntiaddictionFragment;
import com.tds.demo.fragment.BillboardFragment;
import com.tds.demo.fragment.CloudSaveFragment;
import com.tds.demo.fragment.DataSaveFragment;
import com.tds.demo.fragment.GenuineVerifyFragment;
import com.tds.demo.fragment.GiftFragment;
import com.tds.demo.fragment.IM.IMFragment;
import com.tds.demo.fragment.InLineDynamicFragment;
import com.tds.demo.fragment.InsideAccoundFragment;
import com.tds.demo.fragment.LoginFragment;
import com.tds.demo.fragment.pay.PayFragment;
import com.tds.demo.fragment.SupportFragment;
import com.tds.demo.fragment.achievement.AchievementFragment;
import com.tds.demo.fragment.friend.FriendsFragment;
import com.tds.demo.fragment.push.PushFragment;
import com.tds.demo.fragment.ranking.RankingFragment;
import com.tds.demo.until.ToastUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkPermission(this, this);

        initSDK();

        ExpandableListView listView = findViewById(R.id.listview);
        MyBaseExpandableListAdapter adapter
                = new MyBaseExpandableListAdapter(SDKTypeData.getINSTANCE().groupList, SDKTypeData.getINSTANCE().childList, SDKTypeData.getINSTANCE().iconChildList, this);
        listView.setAdapter(adapter);
        listView.setCacheColorHint(0x00000000);
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listView.setGroupIndicator(null);
        adapter.setOnItemClickListener(myItemClickListener);
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            listView.expandGroup(i);
        }


    }

    /**
     * SDK 初始化
     *
     * */
    private void initSDK() {
        Log.e("TAG", "initSDK: "+ Thread.currentThread().getName());


        Set<Pair<String, String>> dimensionSet = new HashSet<>();
        dimensionSet.addAll(Arrays.asList(Pair.create("location", "CN"), Pair.create("platform", "TapTap")));
        String billboardServerUrl = "https://tdsdemo.weijiash.cn"; // 开发者中心 > 你的游戏 > 游戏服务 > 应用配置 > 域名配置 > 公告

        TapBillboardConfig billboardCnConfig = new TapBillboardConfig.Builder()
                .withDimensionSet(dimensionSet)    // 可选
                .withServerUrl(billboardServerUrl) // 必须, 公告的自定义域名
                .build();

        TapDBConfig tapDBConfig = new TapDBConfig();
        tapDBConfig.setEnable(true); //是否开启 TapDB
        tapDBConfig.setChannel("gameChannel"); //分包渠道，长度不大于 256
        tapDBConfig.setGameVersion("1.0.0"); //游戏版本，为空时，自动获取游戏安装包的版本，长度不大于 256


        TapPaymentConfig tapPaymentConfig = new TapPaymentConfig.Builder()
                // 地区暂时只支持「中国地区」
                .withRegionId(Constants.Region.REGION_CN)
                // 语言暂时只支持中文
                .withLanguage(Constants.Language.CN)
                // 微信商户申请H5时提交的授权域名，详见 https://pay.weixin.qq.com/wiki/doc/api/H5.php?chapter=15_4 里 Referer 设置相关部分
                .withWXAuthorizedDomainName("https://tds-payment.tapapis.cn")
                .build();


        TapConfig tdsConfig = new TapConfig.Builder()
                .withAppContext(this)
                .withClientId(SDKInfoData.SDK_CLIENT_ID)
                .withClientToken(SDKInfoData.SDK_CLINT_TOKEN)
                .withServerUrl(SDKInfoData.SDK_SERVER_URL)
                .withBillboardConfig(billboardCnConfig) // 使用公告系统时就必须加入
                .withTapDBConfig(tapDBConfig)
                .withRegionType(TapRegionType.CN)
                .withTapPaymentConfig(tapPaymentConfig)
                .build();

        TapBootstrap.init(MainActivity.this, tdsConfig);

        TapConnect.setEntryVisible(true);

    }

    private MyBaseExpandableListAdapter.OnItemClickListener myItemClickListener = (v, SDKType) -> {
        switch (SDKType){
            case "TapTap登录":
                showFragment(LoginFragment.getInstance(), "loginFragment");

                break;
            case "内嵌动态":
                showFragment(InLineDynamicFragment.getInstance(), "inLineDynamicFragment");

                break;
            case "即时通讯":
                showFragment(IMFragment.getInstance(), "iMFragment");

                break;

            case "正版验证":
                showFragment(GenuineVerifyFragment.getInstance(), "genuineVerifyFragment");

                break;

            case "防沉迷-合规认证":
                showFragment(AntiaddictionFragment.getInstance(), "antiaddictionFragment");
                break;

            case "内建账户":
                showFragment(InsideAccoundFragment.getInstance(), "insideAccoundFragment");
                break;

            case "好友":
                showFragment(FriendsFragment.getInstance(), "friendsFragment");
                break;

            case "成就":
                showFragment(AchievementFragment.getInstance(), "achievementFragment");
                break;

            case "排行榜":

                showFragment(RankingFragment.getInstance(), "rankingFragment");
                break;

            case "云存档":
                showFragment(CloudSaveFragment.getInstance(), "cloudSaveFragment");
                break;

            case "数据存储":
                showFragment(DataSaveFragment.getInstance(), "dataSaveFragment");
                break;
            case "实时语音":
                Log.e("TAG", "onItemClick: "+SDKType );
                break;
            case "推送通知":
                showFragment(PushFragment.getInstance(), "pushFragment");
                break;
            case "公告系统":
                showFragment(BillboardFragment.getInstance(), "billboardFragment");
                break;
            case "礼包系统":
                showFragment(GiftFragment.getInstance(), "giftFragment");
                break;
            case "客服系统":
                showFragment(SupportFragment.getInstance(), "supportFragment");
                break;
            case "支付系统":
                showFragment(PayFragment.getInstance(), "payFragment");
                break;

            default:
                break;

        }


    };

    public void showFragment(Fragment fragment, String tag){

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, null)
                .addToBackStack(tag)
                .commit();
    }


    // 要申请的权限
    public static String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.BLUETOOTH_CONNECT};

    public static void checkPermission(Context context, Activity activity){
        int i = ContextCompat.checkSelfPermission(context, permissions[0]);
        int j = ContextCompat.checkSelfPermission(context, permissions[1]);
        int k = ContextCompat.checkSelfPermission(context, permissions[2]);
        int z = ContextCompat.checkSelfPermission(context, permissions[3]);

        if (i != PackageManager.PERMISSION_GRANTED || j != PackageManager.PERMISSION_GRANTED || k != PackageManager.PERMISSION_GRANTED || z != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissions, 1); //调用方法获取权限
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i =0; i< permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) { //选择了“始终允许”

                } else {
                    ToastUtil.showCus("用户禁止了权限", ToastUtil.Type.WARNING );
                }
            }
        }

    }


}