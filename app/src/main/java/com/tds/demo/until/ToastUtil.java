package com.tds.demo.until;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tds.demo.App;
import com.tds.demo.R;

/**
 * 2022/10/14
 * Describe：自定义 Toast
 *
 */
public class ToastUtil {

    /**
     * Toast类型
     */
    public static enum Type {
        // 成功
        SUCCEED,
        // 错误
        ERROR,
        // 信息，通常
        POINT,
        // 警告
        WARNING;
    }

    /**
     * 系统的Toast
     *
     * @param info
     */
    public static void showToast(String info) {
        Toast.makeText(App.getContextObject(), info, Toast.LENGTH_SHORT).show();
    }

    /**
     * 各种类型的 Toast
     *
     * @param info
     * @param type
     */
    public static void showCus(String info, Type type) {
        Toast toast = new Toast(App.getContextObject());
        View view = null;
        switch (type) {
            case POINT:
                // 提示型Toast
                view = LayoutInflater.from(App.getContextObject()).inflate(R.layout.layout_toast_point, null);
                break;
            case SUCCEED:
                // 操作成功Toast
                view = LayoutInflater.from(App.getContextObject()).inflate(R.layout.layout_taost_succeed, null);
                break;
            case ERROR:
                // 错误Toast
                view = LayoutInflater.from(App.getContextObject()).inflate(R.layout.layout_toast_error, null);
                break;
            case WARNING:
                // 警告Toast
                view = LayoutInflater.from(App.getContextObject()).inflate(R.layout.layout_toast_warning, null);
                break;
            default:
                break;
        }

        if (view != null) {
            TextView tv = view.findViewById(R.id.toast_info);
            tv.setText(info);
            toast.setDuration(Toast.LENGTH_SHORT);
            // 这里是修改Toast的显示位置
            // toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 120);
            toast.setView(view);
            toast.show();
        } else {
            showToast(info);
        }
    }
}

