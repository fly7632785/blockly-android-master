package com.google.blockly.android.demo.bleutils.callback;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * 类名: BleDeviceScanCallback
 * 作者: 陈海明
 * 时间: 2017-08-18 11:05
 * 描述: 扫描成功的回调
 */
public class BleDevceScanCallback implements BluetoothAdapter. LeScanCallback {
    private ScanCallback mScanCallback;

    public BleDevceScanCallback(ScanCallback scanCallback) {
        this.mScanCallback=scanCallback;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (null != mScanCallback) {
            //每次扫描到设备会回调此方法,这里一般做些过滤在添加进list列表
            mScanCallback.onScanning(device, rssi, scanRecord);
        }
    }
}
