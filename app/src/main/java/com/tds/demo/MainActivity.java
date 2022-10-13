package com.tds.demo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.tapsdk.bootstrap.TapBootstrap;
import com.tds.common.entities.TapConfig;
import com.tds.common.models.TapRegionType;
import com.tds.demo.data.SDKInfoData;
import com.tds.demo.data.SDKTypeData;
import com.tds.demo.fragment.AntiaddictionFragment;
import com.tds.demo.fragment.GenuineVerifyFragment;
import com.tds.demo.fragment.InLineDynamicFragment;
import com.tds.demo.fragment.LoginFragment;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
        TapConfig tdsConfig = new TapConfig.Builder()
                .withAppContext(this)
                .withClientId(SDKInfoData.SDK_CLIENT_ID)
                .withClientToken(SDKInfoData.SDK_CLINT_TOKEN)
                .withServerUrl(SDKInfoData.SDK_SERVER_URL)
                .withRegionType(TapRegionType.CN)
                .build();
        TapBootstrap.init(MainActivity.this, tdsConfig);
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
                    Log.e("TAG", "onItemClick: "+SDKType );
                    break;

                case "正版验证":
                    showFragment(GenuineVerifyFragment.getInstance(), "genuineVerifyFragment");
                    break;

                case "防沉迷-合规认证":
                    showFragment(AntiaddictionFragment.getInstance(), "antiaddictionFragment");
                    break;

                case "内建账户":
                    Log.e("TAG", "onItemClick: "+SDKType );
                    break;

                case "好友":
                    Log.e("TAG", "onItemClick: "+SDKType );
                    break;

                case "成就":
                    Log.e("TAG", "onItemClick: "+SDKType );
                    break;

                case "排行榜":
                    Log.e("TAG", "onItemClick: "+SDKType );
                    break;

                case "云存档":
                    Log.e("TAG", "onItemClick: "+SDKType );
                    break;

                case "数据存储":
                    Log.e("TAG", "onItemClick: "+SDKType );
                    break;

                case "云引擎":
                    Log.e("TAG", "onItemClick: "+SDKType );
                    break;

                case "实时语音":
                    Log.e("TAG", "onItemClick: "+SDKType );
                    break;

                case "推送通知":
                    Log.e("TAG", "onItemClick: "+SDKType );
                    break;

                case "公告系统":
                    Log.e("TAG", "onItemClick: "+SDKType );
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



}