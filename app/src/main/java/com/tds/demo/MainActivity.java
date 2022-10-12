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
import com.tds.demo.data.SDKTypeData;
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
                .withAppContext(this) // Context 上下文
                .withClientId("n0bnxy5dfvuq0opmnd") // 必须，开发者中心对应 Client ID
                .withClientToken("iSZ1HmK4nMkFtklge9jlrzTz9dxcV7In5hGI9Gsb") // 必须，开发者中心对应 Client Token
                .withServerUrl("https://n0bnxy5d.cloud.tds1.tapapis.cn") // 必须，开发者中心 > 你的游戏 > 游戏服务 > 基本信息 > 域名配置 > API
                .withRegionType(TapRegionType.CN) // TapRegionType.CN：中国大陆，TapRegionType.IO：其他国家或地区
                .build();
        TapBootstrap.init(MainActivity.this, tdsConfig);
    }

    private MyBaseExpandableListAdapter.OnItemClickListener myItemClickListener = new MyBaseExpandableListAdapter.OnItemClickListener(){
        @Override
        public void onItemClick(View v, String SDKType) {
            Log.e("TAG", "onItemClick:====  "+ SDKType);
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
                    Log.e("TAG", "onItemClick: "+SDKType );
                    break;

                case "防沉迷-合规认证":
                    Log.e("TAG", "onItemClick: "+SDKType );
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