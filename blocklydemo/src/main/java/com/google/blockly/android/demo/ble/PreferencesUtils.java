package com.google.blockly.android.demo.ble;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by Zhangs on 2017/2/21.
 */

public class PreferencesUtils {
    public final static String PREFERENCES_PKG = "ble";
    public final static String SHARE_REFRESH_IS_MANUAL = "isManual";
    //json保存用户信息

    public static void save(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_PKG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void remove(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_PKG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static String get(Context context, String key) {
        if (context == null || TextUtils.isEmpty(key)) {
            return "";
        }
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_PKG, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }
    public static String get(Context context, String key, String def) {
        if (context == null || TextUtils.isEmpty(key)) {
            return "";
        }
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_PKG, Context.MODE_PRIVATE);
        return preferences.getString(key, def);
    }

    public static void saveInt(Context context, String key, int value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_PKG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_PKG, Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }

    public static void saveBool(Context context, String key, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_PKG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBool(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_PKG, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

}
