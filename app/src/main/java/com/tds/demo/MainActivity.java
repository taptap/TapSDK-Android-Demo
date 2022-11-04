package com.tds.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.tapsdk.billboard.Callback;
import com.tapsdk.billboard.TapBillboard;
import com.tapsdk.billboard.exceptions.TapBillboardException;
import com.tapsdk.bootstrap.TapBootstrap;
import com.tds.common.entities.Pair;
import com.tds.common.entities.TapBillboardConfig;
import com.tds.common.entities.TapConfig;
import com.tds.common.models.TapRegionType;
import com.tds.demo.data.SDKInfoData;
import com.tds.demo.data.SDKTypeData;
import com.tds.demo.fragment.BillboardFragment;
import com.tds.demo.fragment.CloudSaveFragment;
import com.tds.demo.fragment.DataSaveFragment;
import com.tds.demo.fragment.IM.IMFragment;
import com.tds.demo.fragment.ranking.RankingFragment;
import com.tds.demo.fragment.achievement.AchievementFragment;
import com.tds.demo.fragment.AntiaddictionFragment;
import com.tds.demo.fragment.GenuineVerifyFragment;
import com.tds.demo.fragment.InLineDynamicFragment;
import com.tds.demo.fragment.InsideAccoundFragment;
import com.tds.demo.fragment.LoginFragment;
import com.tds.demo.fragment.friend.FriendsFragment;
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

        Set<Pair<String, String>> dimensionSet = new HashSet<>();
        dimensionSet.addAll(Arrays.asList(Pair.create("location", "CN"), Pair.create("platform", "TapTap")));
        String billboardServerUrl = "https://tdsdemo.weijiash.cn"; // 开发者中心 > 你的游戏 > 游戏服务 > 应用配置 > 域名配置 > 公告

        TapBillboardConfig billboardCnConfig = new TapBillboardConfig.Builder()
                .withDimensionSet(dimensionSet)    // 可选
                .withServerUrl(billboardServerUrl) // 必须, 公告的自定义域名
                .withTemplate("navigate") // 可选, 默认是 navigate
                .build();


        TapConfig tdsConfig = new TapConfig.Builder()
                .withAppContext(this)
                .withClientId(SDKInfoData.SDK_CLIENT_ID)
                .withClientToken(SDKInfoData.SDK_CLINT_TOKEN)
                .withServerUrl(SDKInfoData.SDK_SERVER_URL)
                .withBillboardConfig(billboardCnConfig) // 使用公告系统时就必须加入
                .withRegionType(TapRegionType.CN)
                .build();
        TapBootstrap.init(MainActivity.this, tdsConfig);

//        TapBillboard.init(tdsConfig);


    }

    private MyBaseExpandableListAdapter.OnItemClickListener myItemClickListener = new MyBaseExpandableListAdapter.OnItemClickListener(){
        @Override
        public void onItemClick(View v, String SDKType) {
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
                    Log.e("TAG", "onItemClick: "+SDKType );
                    break;

                case "公告系统":
                    showFragment(BillboardFragment.getInstance(), "billboardFragment");
                    break;

                default:
                    break;

            }


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
    public static String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE};

    public static void checkPermission(Context context, Activity activity){
        int i = ContextCompat.checkSelfPermission(context, permissions[0]);
        int j = ContextCompat.checkSelfPermission(context, permissions[1]);
        int k = ContextCompat.checkSelfPermission(context, permissions[2]);

        if (i != PackageManager.PERMISSION_GRANTED || j != PackageManager.PERMISSION_GRANTED || k != PackageManager.PERMISSION_GRANTED) {
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