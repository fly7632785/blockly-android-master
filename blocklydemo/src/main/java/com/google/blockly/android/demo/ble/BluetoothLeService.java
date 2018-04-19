package com.google.blockly.android.demo.ble;

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
import android.util.Log;

import java.util.List;

/**
 * created by jafir on 2018/4/19
 */
public class BluetoothLeService {

    private BluetoothManager mBluetoothManager;//用来获取蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;//蓝牙适配器，处理系统蓝牙是否打开，搜索设备
    private BluetoothGatt mBluetoothGatt;//发现蓝牙服务，根据特征值处理数据交互
    private BluetoothDevice mBluetoothDevice = null;//蓝牙设备

    private static BluetoothLeService INSTANCE = null;//单列模式
    private static Context mContext;//上下文

    /**
     * 是否连接
     */
    private boolean isConnected = false;

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    /**
     * 获取上下文
     *
     * @return context
     */
    private Context getContext() {
        return mContext;
    }

    private BluetoothLeService() {
        boolean value = initialize();
        if (!value) Log.e("e","蓝牙适配器adapter初始化失败!!!");
    }

    /**
     * 单列模式，确保唯一性
     *
     * @param context 上下文
     * @return 对象
     */
    public static BluetoothLeService getInstance(Context context) {
        mContext = context;
        if (INSTANCE == null) {
            synchronized (BluetoothLeService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BluetoothLeService();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化bluetooth adapter
     *
     * @return 为null返回false
     */
    private boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        }
        if (mBluetoothManager == null) return false;
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        return mBluetoothAdapter != null;
    }

    /**
     * 启用或者禁用通知\标志返回特性 true, if the requested notification status was set successfully
     *
     * @param characteristic 蓝牙特征对象
     * @param enabled        是否允许
     * @return 设置成功或失败
     */
    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        return !(mBluetoothAdapter == null || mBluetoothGatt == null)
                && mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    public boolean writeDescriptor(BluetoothGattDescriptor bluetoothGattDescriptor) {
        return !(bluetoothGattDescriptor == null || mBluetoothGatt == null)
                && mBluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
    }

    /**
     * 发现数据通道服务
     *
     * @return true or false
     */
    public boolean discoverServices() {
        return !(!isConnected() || mBluetoothGatt == null)
                && mBluetoothGatt.discoverServices();
    }

    /**
     * 读取蓝牙数据
     *
     * @param characteristic 蓝牙特征值
     * @return true or false
     */
    public boolean readCharacteristic(BluetoothGattCharacteristic characteristic) {
        return !(mBluetoothAdapter == null || mBluetoothGatt == null)
                && mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * 往设备中写入数据
     *
     * @param characteristic 蓝牙特征值
     * @return true or false
     */
    public boolean writeCharecteristic(BluetoothGattCharacteristic characteristic) {
        return !(mBluetoothAdapter == null || mBluetoothGatt == null)
                && mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * 获取gatt服务列表
     *
     * @return 远程设备提供的gatt服务列表(功能通道)
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }

    /**
     * 获取RSSI值
     *
     * @return null false
     */
    public boolean getRssiValue() {
        return mBluetoothGatt != null
                && mBluetoothGatt.readRemoteRssi();
    }

    /**
     * 获取蓝牙设备对象
     *
     * @return BluetoothDevice
     */
    public BluetoothDevice getDevice() {
        return mBluetoothDevice;
    }

    /**
     * 判断设备是否连接
     *
     * @param address 设备地址
     * @return true 已连接
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.e("e","蓝牙适配器没有初始化获取mac地址未指明。");
            return false;
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.e("e","蓝牙设备没有发现，无法连接。");
            return false;
        }
        close();//每次连接之前先释放掉原来的资源
        mBluetoothGatt = device.connectGatt(getContext(), false, mGattCallback);//创建新的连接
        return true;
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (mBluetoothGatt != null) mBluetoothGatt.disconnect();
    }

    /**
     * 释放资源
     */
    public void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothDevice = null;
        }
    }

    /**
     * 蓝牙协议回调
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        /**
         * 连接状态
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (mStateChangeListener == null) return;
            if (newState == BluetoothProfile.STATE_CONNECTED) {// 连接状态
                mStateChangeListener.connected(gatt.getDevice());
                setConnected(true);
                mBluetoothDevice = gatt.getDevice();
                discoverServices();//设备连接成功，查找服务!
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {// 断开连接
                close();
                mStateChangeListener.disconnected();
                setConnected(false);
                mBluetoothDevice = null;
            }
        }

        /**
         * 是否发现服务
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (mCharacteristicListener != null)
                    mCharacteristicListener.onServicesDiscovered();
            } else {
                Log.e("e","蓝牙通信服务回调失败：status=" + status);
            }
        }

        /**
         * 读操作回调
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (mCharacteristicListener != null) {
                mCharacteristicListener.onCharacteristicRead(gatt, characteristic, status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (mCharacteristicListener != null) {
                mCharacteristicListener.onCharacteristicChanged(gatt, characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (mCharacteristicListener != null) {
                mCharacteristicListener.onCharacteristicWrite(gatt, characteristic, status);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.e("e","onDescriptorWrite: status=" + status + "   uuid=" + descriptor.getUuid().toString());
        }

        /**
         * 信号强度
         */
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if (mCharacteristicListener != null) {
                mCharacteristicListener.onReadRemoteRssi(gatt, rssi, status);
            }
        }
    };


    /**
     * 连接状态接口
     */
    public interface OnConnectionStateChangeListener {
        void connected(BluetoothDevice device);

        void disconnected();
    }

    /**
     * 发现服务、数据读写操作接口
     */
    public interface OnCharacteristicListener {
        void onServicesDiscovered();

        void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

        void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);

        void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);

        void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);
    }

    private OnConnectionStateChangeListener mStateChangeListener = null;
    private OnCharacteristicListener mCharacteristicListener = null;

    public void setOnConnectionStateChangeListener(OnConnectionStateChangeListener listener) {
        this.mStateChangeListener = listener;
    }

    public void setOnCharacteristicListener(OnCharacteristicListener listener) {
        this.mCharacteristicListener = listener;
    }
}
