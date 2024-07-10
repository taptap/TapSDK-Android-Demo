package com.tds.demo;
import android.app.Application;
import android.content.Context;
import android.view.View;

import com.tapsdk.lc.LCLogger;
import com.tapsdk.lc.LeanCloud;
import com.tds.demo.data.SDKInfoData;

/**
 * 2022/10/14
 * Describe：
 */
public class App extends Application {

    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
//
        LeanCloud.initialize(this, SDKInfoData.SDK_CLIENT_ID, SDKInfoData.SDK_CLINT_TOKEN, SDKInfoData.SDK_SERVER_URL);
        LeanCloud.setLogLevel(LCLogger.Level.DEBUG);

    }

    public static Context getContextObject(){
        return context;
    }

}