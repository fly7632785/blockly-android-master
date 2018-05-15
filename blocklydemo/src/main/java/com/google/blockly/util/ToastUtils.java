package com.google.blockly.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * created by jafir on 2018/4/14
 */
public class ToastUtils {
    public static Context mContext;

    /**
     * 在Application 中注册。
     *
     * @param context
     */
    public static void register(Context context) {
        mContext = context;
    }

    public static void show(int resId) {
        if (mContext == null) {
            throw new NullPointerException("please register context firstly.");
        } else {
            Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
        }

    }

    public static void show(String string) {
        if (mContext == null) {
            throw new NullPointerException("please register context firstly.");
        }
        if (TextUtils.isEmpty(string)) {
            return;
        }
        Toasty.info(mContext, string,Toast.LENGTH_LONG,false).show();
    }

    public static void show(String string, int duration) {
        if (mContext == null) {
            throw new NullPointerException("please register context firstly.");
        }
        if (TextUtils.isEmpty(string)) {
            return;
        }
        Toast.makeText(mContext, string, duration).show();
    }

}