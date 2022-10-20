package com.tds.demo;

import android.app.Application;
import android.content.Context;

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
    }

    public static Context getContextObject(){
        return context;
    }

}
