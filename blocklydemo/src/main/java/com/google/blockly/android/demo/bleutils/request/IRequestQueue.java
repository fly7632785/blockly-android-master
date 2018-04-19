package com.google.blockly.android.demo.bleutils.request;

/**
 * 类名: IRequestQueue
 * 作者: 陈海明
 * 时间: 2017-08-18 13:52
 * 描述: NULL
 */
public interface IRequestQueue<T> {
    void set(String key, T t);

    T get(String key);
}
