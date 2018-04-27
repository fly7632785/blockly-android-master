package com.google.blockly;

import android.app.Application;
import android.content.Context;

import com.google.blockly.util.ToastUtils;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.internal.RxBleLog;
import com.tencent.bugly.crashreport.CrashReport;

import io.reactivex.Observable;

/**
 * created by jafir on 2018/4/14
 */
public class MainApplication extends Application {
    public static String address;
    public Observable<RxBleConnection> connectObserverable;
    private RxBleClient rxBleClient;

    public RxBleDevice bleDevice;

    /**
     * In practise you will use some kind of dependency injection pattern.
     */
    public static RxBleClient getRxBleClient(Context context) {
        MainApplication application = (MainApplication) context.getApplicationContext();
        return application.rxBleClient;
    }

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
        rxBleClient = RxBleClient.create(this);
        RxBleClient.setLogLevel(RxBleLog.VERBOSE);
    }
}
