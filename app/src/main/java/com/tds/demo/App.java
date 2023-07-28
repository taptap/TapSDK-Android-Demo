package com.tds.demo;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.tds.demo.data.SDKInfoData;
import com.tds.demo.fragment.push.PushActivity;

import cn.leancloud.LCInstallation;
import cn.leancloud.LCLogger;
import cn.leancloud.LCObject;
import cn.leancloud.LCUser;
import cn.leancloud.LeanCloud;
import cn.leancloud.push.PushService;
import cn.leancloud.utils.StringUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 2022/10/14
 * Describe：
 */
public class App extends Application {

    private static Context context;
    private static String installationId;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LeanCloud.initialize(this, SDKInfoData.SDK_CLIENT_ID, SDKInfoData.SDK_CLINT_TOKEN, SDKInfoData.SDK_SERVER_URL);
        LeanCloud.setLogLevel(LCLogger.Level.DEBUG);

        // android 8.0 以上必须实现一个或多个通知渠道
        createNotificationChannel("public", "普通通知", "public_notificationId");
        createNotificationChannel("other", "其他通知", "other_notificationId");

        // 推送订阅频道，需要在保存 Installation 前调用
        // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
        PushService.subscribe(this, "other", PushActivity.class);

        // 设置通知展示的默认 channel，否则消息无法展示。
        PushService.setDefaultChannelId(this, "other_notificationId");
        // 推送使用这段代码应该在应用启动的时候调用一次，保证设备注册到云端。你可以监听调用回调，获取 installationId 做数据关联。
        LCInstallation.getCurrentInstallation().saveInBackground().subscribe(new Observer<LCObject>() {
            @Override
            public void onSubscribe(Disposable d) {
            }
            @Override
            public void onNext(LCObject avObject) {
                // 关联 installationId 到用户表等操作。
                installationId = LCInstallation.getCurrentInstallation().getInstallationId();
                Log.e("TAG", "设备的 installationId: "+ installationId );
            }
            @Override
            public void onError(Throwable e) {
                System.out.println("保存失败，错误信息：" + e.getMessage());
            }
            @Override
            public void onComplete() {
            }
        });
    }

    public static Context getContextObject(){
        return context;
    }

    public static String getInstallationId(){
        return installationId;
    }




    /**
     * 创建通知渠道
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
