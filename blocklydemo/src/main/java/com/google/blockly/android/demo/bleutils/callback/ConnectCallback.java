package com.google.blockly.android.demo.bleutils.callback;

/**
 * 类名: ConnectCallback
 * 作者: 陈海明
 * 时间: 2017-08-18 13:53
 * 描述: 连接回到
 */
public interface ConnectCallback {
    /**
     *  获得通知之后
     */

    void onConnSuccess();

    /**
     * 断开或连接失败
     */
    void onConnFailed();
}
