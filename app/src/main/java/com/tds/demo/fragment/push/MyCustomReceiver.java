package com.tds.demo.fragment.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 2022/11/8
 * Describe：
 */
public class MyCustomReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 获取推送消息数据
        String message = intent.getStringExtra("com.avoscloud.Data");
        String channel = intent.getStringExtra("com.avoscloud.Channel");
        System.out.println("message=" + message + ", channel=" + channel);
        Log.e("TAG", "onReceive: "+message+ channel );
    }
}