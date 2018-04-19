package com.google.blockly.android.demo.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * created by jafir on 2018/4/19
 */
public interface IBluzScanHelper {
    /**
     * 注册蓝牙回调广播
     *
     * @param context 上下文
     */
    void registBroadcast(Context context);

    /**
     * 注销蓝牙回调广播
     *
     * @param context 上下文
     */
    void unregistBroadcast(Context context);

    /**
     * 获取设备使能信息
     */
    boolean isEnabled();

    /**
     * 关闭蓝牙
     */
    boolean disable();

    /**
     * 打开蓝牙
     */
    boolean enable();

    /**
     * 打开蓝牙提示框
     */
    void openBluetooth();

    /**
     * 开始扫描蓝牙设备
     */
    void startDiscovery();

    /**
     * 取消扫描
     */
    void cancelDiscovery();

    /**
     * 连接蓝牙设备，当前应用所需的Profile
     */
    boolean connect(BluetoothDevice device);

    /**
     * 断开全部连接
     */
    void disconnect();


    /**
     * 设置搜索设备回调监听
     */
    void addOnDiscoveryListener(OnDiscoveryListener listener);

    void removeOnDiscoveryListener(OnDiscoveryListener listener);

    /**
     * 设置连接回调监听
     */
    void addOnConnectionListener(OnConnectionListener listener);

    void removeOnConnectionListener(OnConnectionListener listener);

    /**
     * 断开数据连接，释放资源
     */
    void release();

    /**
     * 获取当前已连接的设备
     */
    BluetoothDevice getConnectedDevice();


    /**
     * 查找蓝牙回调接口
     */
    interface OnDiscoveryListener {
        /**
         * 找到设备
         */
        void onFound(BluetoothDevice device);

        /**
         * 搜索结束
         */
        void onDiscoveryFinished();
    }

    /**
     * 连接回调接口
     */
    interface OnConnectionListener {
        /**
         * 已连接
         */
        void onConnected(BluetoothDevice device);

        /**
         * 已断开连接
         */
        void onDisconnected(BluetoothDevice device);
    }
}
