package com.google.blockly.android.demo.robot;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.blockly.android.demo.R;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.scan.ScanResult;

/**
 * created by jafir on 2018/3/27
 */
public class RxDeviceAdapter extends BaseQuickAdapter<ScanResult, BaseViewHolder> {
    @Override
    protected void convert(BaseViewHolder helper, ScanResult item) {
        helper.setText(R.id.address, "地址：" + getAddress(item.getBleDevice()));
        helper.setText(R.id.name, "名字：" + item.getBleDevice().getName() + getConnectStr(item.getBleDevice()));
//        helper.setText(R.id.name, "名字：" + item.getBleDevice().getName() + getConnectStr(item.getBleDevice())+"\t"+getDistance( item.getRssi()));
    }

    private String getAddress(RxBleDevice bleDevice) {
        return TextUtils.isEmpty(bleDevice.getMacAddress()) ? "" : bleDevice.getMacAddress();
    }

    private String getConnectStr(RxBleDevice bleDevice) {
        return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED ? "(已连接)" : "";
    }

    private static final double A_Value = 60; // A - 发射端和接收端相隔1米时的信号强度
    private static final double n_Value = 2.0; //  n - 环境衰减因子

    public static double getDistance(int rssi) { //根据Rssi获得返回的距离,返回数据单位为m
        int iRssi = Math.abs(rssi);
        double power = (iRssi - A_Value) / (10 * n_Value);
        return Math.pow(10, power);
    }

    public RxDeviceAdapter(int layoutResId) {
        super(layoutResId);
    }

}
