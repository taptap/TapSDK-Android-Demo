package com.tds.demo;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.tapsdk.bootstrap.TapBootstrap;
import com.tds.common.entities.Pair;
import com.tds.common.entities.TapBillboardConfig;
import com.tds.common.entities.TapConfig;
import com.tds.common.models.TapRegionType;
import com.tds.demo.data.SDKInfoData;
import com.tds.demo.data.SDKTypeData;
import com.tds.demo.fragment.AntiaddictionFragment;
import com.tds.demo.fragment.BillboardFragment;
import com.tds.demo.fragment.CloudSaveFragment;
import com.tds.demo.fragment.DataSaveFragment;
import com.tds.demo.fragment.GenuineVerifyFragment;
import com.tds.demo.fragment.IM.IMFragment;
import com.tds.demo.fragment.InLineDynamicFragment;
import com.tds.demo.fragment.InsideAccoundFragment;
import com.tds.demo.fragment.LoginFragment;
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
    private WebView webView;
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
     * SDK ?????????
     *
     * */
    private void initSDK() {

        Set<Pair<String, String>> dimensionSet = new HashSet<>();
        dimensionSet.addAll(Arrays.asList(Pair.create("location", "CN"), Pair.create("platform", "TapTap")));
        String billboardServerUrl = "https://tdsdemo.weijiash.cn"; // ??????????????? > ???????????? > ???????????? > ???????????? > ???????????? > ??????

        TapBillboardConfig billboardCnConfig = new TapBillboardConfig.Builder()
                .withDimensionSet(dimensionSet)    // ??????
                .withServerUrl(billboardServerUrl) // ??????, ????????????????????????
                .withTemplate("navigate") // ??????, ????????? navigate
                .build();


        TapConfig tdsConfig = new TapConfig.Builder()
                .withAppContext(this)
                .withClientId(SDKInfoData.SDK_CLIENT_ID)
                .withClientToken(SDKInfoData.SDK_CLINT_TOKEN)
                .withServerUrl(SDKInfoData.SDK_SERVER_URL)
                .withBillboardConfig(billboardCnConfig) // ????????????????????????????????????
                .withRegionType(TapRegionType.CN)
                .build();
        TapBootstrap.init(MainActivity.this, tdsConfig);

//        TapBillboard.init(tdsConfig);

    }

    private MyBaseExpandableListAdapter.OnItemClickListener myItemClickListener = new MyBaseExpandableListAdapter.OnItemClickListener(){
        @Override
        public void onItemClick(View v, String SDKType) {
            switch (SDKType){
                case "TapTap??????":
                    showFragment(LoginFragment.getInstance(), "loginFragment");

                    break;
                case "????????????":
                    showFragment(InLineDynamicFragment.getInstance(), "inLineDynamicFragment");
                    break;
                case "????????????":
                    showFragment(IMFragment.getInstance(), "iMFragment");
                    break;

                case "????????????":
                    showFragment(GenuineVerifyFragment.getInstance(), "genuineVerifyFragment");
                    break;

                case "?????????-????????????":
                    showFragment(AntiaddictionFragment.getInstance(), "antiaddictionFragment");
                    break;

                case "????????????":
                    showFragment(InsideAccoundFragment.getInstance(), "insideAccoundFragment");
                    break;

                case "??????":
                    showFragment(FriendsFragment.getInstance(), "friendsFragment");
                    break;

                case "??????":
                    showFragment(AchievementFragment.getInstance(), "achievementFragment");
                    break;

                case "?????????":

                    showFragment(RankingFragment.getInstance(), "rankingFragment");
                    break;

                case "?????????":
                    showFragment(CloudSaveFragment.getInstance(), "cloudSaveFragment");
                    break;

                case "????????????":
                    showFragment(DataSaveFragment.getInstance(), "dataSaveFragment");
                    break;

                case "????????????":
                    Log.e("TAG", "onItemClick: "+SDKType );
                    break;

                case "????????????":
                    showFragment(PushFragment.getInstance(), "pushFragment");
                    break;

                case "????????????":
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


    // ??????????????????
    public static String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE};

    public static void checkPermission(Context context, Activity activity){
        int i = ContextCompat.checkSelfPermission(context, permissions[0]);
        int j = ContextCompat.checkSelfPermission(context, permissions[1]);
        int k = ContextCompat.checkSelfPermission(context, permissions[2]);

        if (i != PackageManager.PERMISSION_GRANTED || j != PackageManager.PERMISSION_GRANTED || k != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissions, 1); //????????????????????????
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i =0; i< permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) { //???????????????????????????

                } else {
                    ToastUtil.showCus("?????????????????????", ToastUtil.Type.WARNING );
                }
            }
        }

    }




    /**
     * ??????????????????
     *
     * */
    private void createNotificationChannel(String name, String description, String notificationId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(notificationId, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}