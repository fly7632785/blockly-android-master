package com.google.blockly.android.demo.robot;

import java.util.UUID;

/**
 * created by jafir on 2018/4/17
 */
public class BleUUID {
    //此属性一般不用修改
    private static final UUID BLUETOOTH_NOTIFY_D = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    //TODO 以下uuid根据公司硬件改变
    public static final UUID UUID_SERVICE = UUID.fromString("0000fee0-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_INDICATE = UUID.fromString("0000000-0000-0000-8000-00805f9b0000");
    public static final UUID UUID_NOTIFY = UUID.fromString("0000fee1-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_WRITE = UUID.fromString("0000fee2-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_READ = UUID.fromString("3f3e3d3c-3b3a-3938-3736-353433323130");
}
