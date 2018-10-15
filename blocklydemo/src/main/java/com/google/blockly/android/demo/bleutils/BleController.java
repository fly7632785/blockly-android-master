package com.google.blockly.android.demo.bleutils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.blockly.android.demo.bleutils.callback.BleDevceScanCallback;
import com.google.blockly.android.demo.bleutils.callback.ConnectCallback;
import com.google.blockly.android.demo.bleutils.callback.OnReceiverCallback;
import com.google.blockly.android.demo.bleutils.callback.OnWriteCallback;
import com.google.blockly.android.demo.bleutils.callback.ScanCallback;
import com.google.blockly.android.demo.bleutils.request.ReceiverRequestQueue;
import com.google.blockly.util.ToastUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BleController {

    private static final String TAG = "BleController";

    private static BleController mBleController;
    private Context mContext;

    private BluetoothManager mBlehManager;
    private BluetoothAdapter mBleAdapter;
    private BluetoothGatt mBleGatt;
    private BluetoothGattCharacteristic mBleGattCharacteristic;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private BleGattCallback mGattCallback;
    private OnWriteCallback writeCallback;

    private boolean mScanning;


    //默认扫描时间：15s
    private static final int SCAN_TIME = 10000;
    //默认连接超时时间:5s
    private static final int CONNECTION_TIME_OUT = 10000;
    //获取到所有服务的集合
    private HashMap<String, Map<String, BluetoothGattCharacteristic>> servicesMap = new HashMap<>();

    //连接请求是否ok
    private boolean isConnectok;
    //是否是用户手动断开
    private boolean isMybreak = false;
    //连接结果的回调
    private ConnectCallback connectCallback;
    //读操作请求队列
    private ReceiverRequestQueue mReceiverRequestQueue = new ReceiverRequestQueue();
    public final static String[] error = new String[]{
            "蓝牙未开启",
            "服务无效",
            "特征无效",
            "操作失败"
    };

    //此属性一般不用修改
    private static final String BLUETOOTH_NOTIFY_D = "00002902-0000-1000-8000-00805f9b34fb";
    //TODO 以下uuid根据公司硬件改变
    public static final String UUID_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static final String UUID_INDICATE = "0000000-0000-0000-8000-00805f9b0000";
    public static final String UUID_NOTIFY = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static final String UUID_WRITE = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static final String UUID_READ = "3f3e3d3c-3b3a-3938-3736-353433323130";
    private boolean isConnected;
    private String connectedAdress;
    private BluetoothDevice remoteDevice;

    public static synchronized BleController getInstance() {
        if (null == mBleController) {
            mBleController = new BleController();
        }
        return mBleController;
    }

    public boolean isConnected() {
        //todo mock
        return isConnected;
    }

    public String getConnectedAdress() {
        return connectedAdress;
    }

    public boolean isScanning() {
        return mScanning;
    }

    public BleController initble(Context context) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
            mBlehManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (null == mBlehManager) {
                ToastUtils.show("此手机没有发现蓝牙模块");
                Log.e(TAG, "BluetoothManager init error!");
            }

            mBleAdapter = mBlehManager.getAdapter();
            if (mBleAdapter == null || !mBleAdapter.isEnabled()) {
                ToastUtils.show("此手机没有发现蓝牙模块");
                Log.e(TAG, "mBleAdapter init error!");
            }

            mGattCallback = new BleGattCallback();
        }
        return this;
    }


    /**
     * 扫描设备
     *
     * @param time         指定扫描时间
     * @param scanCallback 扫描回调
     */
    public void ScanBle(int time, final boolean enable, final ScanCallback scanCallback) {
        if (!isEnable()) {
            mBleAdapter.enable();
            Log.e(TAG, "Bluetooth is not open!");
        }
        final BleDevceScanCallback bleDeviceScanCallback = new BleDevceScanCallback(scanCallback);
        if (enable) {
            if (mScanning) return;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    //time后停止扫描
                    mBleAdapter.stopLeScan(bleDeviceScanCallback);
                    scanCallback.onSuccess();
                }
            }, time <= 0 ? SCAN_TIME : time);
            mScanning = true;
            mBleAdapter.startLeScan(bleDeviceScanCallback);
        } else {
            mScanning = false;
            mBleAdapter.stopLeScan(bleDeviceScanCallback);
        }
    }


    /**
     * 扫描设备
     * 默认扫描10s
     *
     * @param scanCallback
     */
    public void ScanBle(final boolean enable, final ScanCallback scanCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ScanBle(SCAN_TIME, enable, scanCallback);
            }
        }).start();

    }


    /**
     * 连接设备
     *
     * @param connectionTimeOut 指定连接超时
     * @param address           设备mac地址
     * @param connectCallback   连接回调
     */
    public void Connect(final int connectionTimeOut, final String address, ConnectCallback connectCallback) {

        if (mBleAdapter == null || address == null) {
            Log.e(TAG, "No device found at this address：" + address);
            return;
        }
        remoteDevice = mBleAdapter.getRemoteDevice(address);
        if (remoteDevice == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return;
        }
        this.connectCallback = connectCallback;
        if (mBleGatt != null) {
            mBleGatt.disconnect();
        }
        mBleGatt = remoteDevice.connectGatt(mContext, false, mGattCallback);
        Log.e(TAG, "connecting mac-address:" + address);
        delayConnectResponse(connectionTimeOut);
    }

    /**
     * 连接设备
     *
     * @param address         设备mac地址
     * @param connectCallback 连接回调
     */
    public void Connect(final String address, ConnectCallback connectCallback) {
        Connect(CONNECTION_TIME_OUT, address, connectCallback);
    }

    /**
     * 发送数据
     *
     * @param value         指令
     * @param writeCallback 发送回调
     */
    public void WriteBuffer(String value, OnWriteCallback writeCallback) {
        this.writeCallback = writeCallback;
        if (!isEnable()) {
            writeCallback.onFailed(OnWriteCallback.FAILED_BLUETOOTH_DISABLE);
            Log.e(TAG, "FAILED_BLUETOOTH_DISABLE");
            return;
        }

        mBleGattCharacteristic = getBluetoothGattCharacteristic(UUID_SERVICE, UUID_WRITE);

        if (null == mBleGattCharacteristic) {
            writeCallback.onFailed(OnWriteCallback.FAILED_INVALID_CHARACTER);
            Log.e(TAG, "FAILED_INVALID_CHARACTER");
            return;
        }

//        mBleGattCharacteristic.setValue(HexUtil.hexStringToBytes(value)); //不使用此方法  只需 直接getBytes
        mBleGattCharacteristic.setValue(value.getBytes());
        //发送

        boolean b = mBleGatt.writeCharacteristic(mBleGattCharacteristic);

        Log.e(TAG, "send:" + b + "data：" + value);
    }

    /**
     * 设置读取数据的监听
     *
     * @param requestKey
     * @param onReceiverCallback
     */
    public void RegistReciveListener(String requestKey, OnReceiverCallback onReceiverCallback) {
        mReceiverRequestQueue.set(requestKey, onReceiverCallback);
    }

    /**
     * 移除读取数据的监听
     *
     * @param requestKey
     */
    public void UnregistReciveListener(String requestKey) {
        mReceiverRequestQueue.removeRequest(requestKey);
    }

    /**
     * 手动断开Ble连接
     */
    public void disConnectBleConn() {
        disConnection();
        isMybreak = true;
        isConnected = false;
    }


//------------------------------------分割线--------------------------------------

    /**
     * 当前蓝牙是否打开
     */
    private boolean isEnable() {
        if (null != mBleAdapter) {
            return mBleAdapter.isEnabled();
        }
        return false;
    }

    /**
     * 重置数据
     */
    private void reset() {
        isConnectok = false;
        Log.e(TAG, "isConnectok    not ok");
        servicesMap.clear();
    }

    /**
     * 超时断开
     *
     * @param connectionTimeOut
     */
    private void delayConnectResponse(int connectionTimeOut) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "isConnectok" + isConnectok);
                if (!isConnectok && !isMybreak) {
                    Log.e(TAG, "connect timeout");
                    disConnection();
                    reConnect();
                } else {
                    isMybreak = false;
                }
            }
        }, connectionTimeOut <= 0 ? CONNECTION_TIME_OUT : connectionTimeOut);
    }

    /**
     * 断开连接
     */
    private void disConnection() {
        if (null == mBleAdapter || null == mBleGatt) {
            Log.e(TAG, "disconnection error maybe no init");
            return;
        }
        mBleGatt.disconnect();
        reset();
    }

    /**
     * 蓝牙GATT连接及操作事件回调
     */
    private class BleGattCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) { //连接成功
                isMybreak = false;
                isConnectok = true;
                Log.e(TAG, "STATE_CONNECTED isConnectok    ok");
                mBleGatt.discoverServices();
                connSuccess();
                connectedAdress = gatt.getDevice().getAddress();
                isConnected = true;
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {   //断开连接
                Log.e(TAG, "断开连接");
                isConnected = false;
                connectedAdress = null;
                mBleGatt.close();
                if (!isMybreak) {
                    reConnect();
                }
                reset();
            }
        }

        //发现新服务
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (null != mBleGatt && status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "onServicesDiscovered");
                List<BluetoothGattService> services = mBleGatt.getServices();
                for (int i = 0; i < services.size(); i++) {
                    HashMap<String, BluetoothGattCharacteristic> charMap = new HashMap<>();
                    BluetoothGattService bluetoothGattService = services.get(i);
                    String serviceUuid = bluetoothGattService.getUuid().toString();
                    Log.e(TAG, "serviceUuid:"+serviceUuid);
                    List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
                    for (int j = 0; j < characteristics.size(); j++) {
                        charMap.put(characteristics.get(j).getUuid().toString(), characteristics.get(j));
                    }
                    servicesMap.put(serviceUuid, charMap);
                }
                Log.e(TAG, "servicesMap size" + servicesMap.size());
                BluetoothGattCharacteristic NotificationCharacteristic = getBluetoothGattCharacteristic(UUID_SERVICE, UUID_NOTIFY);
                if (NotificationCharacteristic == null)
                    return;
                Log.e(TAG, "enableNotification");
                enableNotification(true, NotificationCharacteristic);
            }
        }

        //读数据
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        //写数据
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (null != writeCallback) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            writeCallback.onSuccess();
                        }
                    });
                    Log.e(TAG, "Send data success!");
                } else {
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            writeCallback.onFailed(OnWriteCallback.FAILED_OPERATION);
                        }
                    });
                    Log.e(TAG, "Send data failed!");
                }
            }
        }

        //通知数据
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (null != mReceiverRequestQueue) {
                HashMap<String, OnReceiverCallback> map = mReceiverRequestQueue.getMap();
                final byte[] rec = characteristic.getValue();
                for (String key : mReceiverRequestQueue.getMap().keySet()) {
                    final OnReceiverCallback onReceiverCallback = map.get(key);
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            onReceiverCallback.onReceiver(rec);
                        }
                    });
                }
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }
    }

    /**
     * 设置通知
     *
     * @param enable         true为开启false为关闭
     * @param characteristic 通知特征
     * @return
     */
    private boolean enableNotification(boolean enable, BluetoothGattCharacteristic characteristic) {
        if (mBleGatt == null || characteristic == null)
            return false;
        if (!mBleGatt.setCharacteristicNotification(characteristic, enable))
            return false;
        BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString(BLUETOOTH_NOTIFY_D));
        if (clientConfig == null)
            return false;

        if (enable) {
            clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        return mBleGatt.writeDescriptor(clientConfig);
    }

    public BluetoothGattService getService(UUID uuid) {
        if (mBleAdapter == null || mBleGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return null;
        }
        return mBleGatt.getService(uuid);
    }


    /**
     * 根据服务UUID和特征UUID,获取一个特征{@link BluetoothGattCharacteristic}
     *
     * @param serviceUUID   服务UUID
     * @param characterUUID 特征UUID
     */
    private BluetoothGattCharacteristic getBluetoothGattCharacteristic(String serviceUUID, String characterUUID) {
        if (!isEnable()) {
            throw new IllegalArgumentException(" Bluetooth is no enable please call BluetoothAdapter.enable()");
        }
        if (null == mBleGatt) {
            Log.e(TAG, "mBluetoothGatt is null");
            return null;
        }

        //找服务
        Map<String, BluetoothGattCharacteristic> bluetoothGattCharacteristicMap = servicesMap.get(serviceUUID);
        if (null == bluetoothGattCharacteristicMap) {
            Log.e(TAG, "Not found the serviceUUID!");
            return null;
        }

        //找特征
        Set<Map.Entry<String, BluetoothGattCharacteristic>> entries = bluetoothGattCharacteristicMap.entrySet();
        BluetoothGattCharacteristic gattCharacteristic = null;
        for (Map.Entry<String, BluetoothGattCharacteristic> entry : entries) {
            if (characterUUID.equals(entry.getKey())) {
                gattCharacteristic = entry.getValue();
                break;
            }
        }
        return gattCharacteristic;
    }


    private void runOnMainThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            if (mHandler != null) {
                mHandler.post(runnable);
            }
        }
    }

    private boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }


    // TODO 此方法断开连接或连接失败时会被调用。可在此处理自动重连,内部代码可自行修改，如发送广播
    private void reConnect() {
        if (connectCallback != null) {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    connectCallback.onConnFailed();
                }
            });
        }

        Log.e(TAG, "Ble disconnect or connect failed!");
    }

    // TODO 此方法Notify成功时会被调用。可在通知界面连接成功,内部代码可自行修改，如发送广播
    private void connSuccess() {
        if (connectCallback != null) {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    connectCallback.onConnSuccess();
                }
            });
        }
        Log.e(TAG, "Ble connect success!");
    }

}
