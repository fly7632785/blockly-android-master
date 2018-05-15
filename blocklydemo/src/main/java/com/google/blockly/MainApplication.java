package com.google.blockly;

import android.app.Application;

import com.google.blockly.android.demo.R;
import com.google.blockly.util.ToastUtils;
import com.tencent.bugly.crashreport.CrashReport;

import es.dmoral.toasty.Toasty;

/**
 * created by jafir on 2018/4/14
 */
public class MainApplication extends Application {
    public static String address;

    private static MainApplication instance;

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CrashReport.initCrashReport(getApplicationContext(), "0638dbcde3", true);
        ToastUtils.register(this);

        Toasty.Config.getInstance()
                .setInfoColor(getResources().getColor(R.color.toast_color)) // optional
                .setTextColor(getResources().getColor(R.color.white)) // optional
                .apply(); // required
    }
}
