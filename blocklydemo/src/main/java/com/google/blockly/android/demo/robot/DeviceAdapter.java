package com.google.blockly.android.demo.robot;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.blockly.android.demo.R;
import com.google.blockly.android.demo.bleutils.BleController;

import java.util.List;

/**
 * created by jafir on 2018/3/27
 */
public class DeviceAdapter extends BaseQuickAdapter<BluetoothDevice, BaseViewHolder> {
    @Override
    protected void convert(BaseViewHolder helper, BluetoothDevice item) {
        if (!TextUtils.isEmpty(item.getName())) {
            if (item.getAddress().equals(BleController.getInstance().getConnectedAdress()) && BleController.getInstance().isConnected()) {
                helper.setText(R.id.name, item.getName() + "(已连接)");
                helper.itemView.setBackgroundResource(R.color.selected_color);
            } else {
                helper.itemView.setBackgroundResource(R.color.white);
                helper.setText(R.id.name, "" + item.getName());
            }
        } else {
            if (item.getAddress().equals(BleController.getInstance().getConnectedAdress()) && BleController.getInstance().isConnected()) {
                helper.setText(R.id.name, item.getAddress() + "(已连接)");
                helper.itemView.setBackgroundResource(R.color.selected_color);
            } else {
                helper.itemView.setBackgroundResource(R.color.white);
                helper.setText(R.id.name, "" + item.getAddress());
            }
        }
    }

    private static final double A_Value = 60; // A - 发射端和接收端相隔1米时的信号强度
    private static final double n_Value = 2.0; //  n - 环境衰减因子

    public static double getDistance(int rssi) { //根据Rssi获得返回的距离,返回数据单位为m
        int iRssi = Math.abs(rssi);
        double power = (iRssi - A_Value) / (10 * n_Value);
        return Math.pow(10, power);
    }

    public DeviceAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }

}
