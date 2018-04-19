package com.google.blockly.android.demo.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import com.google.blockly.util.HexUtil;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;

/**
 * created by jafir on 2018/4/19
 */
public class BluzManager implements BluetoothLeService.OnCharacteristicListener {
    private static BluzManager INSTANCE = null;
    private Context mContext;
    private BluetoothLeService mBluetoothLeService;

    /**
     * 缓存连接设备的蓝牙特征值和uuid
     */
    private HashMap<UUID, BluetoothGattCharacteristic> mWriteHashMap = new HashMap<>();

    private Context getContext() {
        return mContext;
    }

    /**
     * 蓝牙信号强度回调接口
     */
    private OnReadRemoteRssiListener listener;

    public interface OnReadRemoteRssiListener {
        void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);
    }

    public void setOnReadRemoteRssiListener(OnReadRemoteRssiListener listener) {
        this.listener = listener;
    }

    private BluzManager(Context context) {
        mContext = context;
        initialize();
    }

    /**
     * 单列模式，确保唯一性
     *
     * @param context 上下文
     * @return BluzManager蓝牙管理类
     */
    public static BluzManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (BluzManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BluzManager(context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化相关数据
     */
    private void initialize() {
        //初始化蓝牙服务类
        mBluetoothLeService = BluetoothLeService.getInstance(getContext());
        mBluetoothLeService.setOnCharacteristicListener(this);
    }

    /**
     * 获取蓝牙信号强度,只能单次获取
     *
     * @return boolean
     */
    public boolean getRssiValue() {
        return mBluetoothLeService.getRssiValue();
    }

    /**
     * 清楚所有缓存数据
     */
    private void clearHashMap() {
        mWriteHashMap.clear();
    }


    public void write(byte[] bytes) {
        BluetoothGattCharacteristic characteristic = mWriteHashMap.get(BluetoothAttributes.UUID_WRITE);
        characteristic.setValue(bytes);
        mBluetoothLeService.writeCharecteristic(characteristic);
    }

    /**
     * 蓝牙服务发现回调
     */
    @Override
    public void onServicesDiscovered() {
        clearHashMap();
        List<BluetoothGattService> list = mBluetoothLeService.getSupportedGattServices();
        for (BluetoothGattService bluetoothGattService : list) {
            UUID uuid = bluetoothGattService.getUuid();
            Log.d("d", "蓝牙服务回调 uuid=" + uuid.toString());
            //根据底层提供的可用的特征服务uuid过滤出可用的服务以及特征值
            if (BluetoothAttributes.UUID_CHARACTERISTICS_SERVICE.equalsIgnoreCase(uuid.toString())) {
                List<BluetoothGattCharacteristic> characteristicList = bluetoothGattService.getCharacteristics();
                for (BluetoothGattCharacteristic characteristic : characteristicList) {
                    //根据服务特征中的属性区分是可读、可写、通知。
                    int properties = characteristic.getProperties();
                    //拥有写权限的uuid放入集合中缓存起来，在需要使用的时候拿取出来。
                    if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0) {
                        mWriteHashMap.put(characteristic.getUuid(), characteristic);
                    }
                    //打开通知权限，以下BluetoothAttributes.UUID_RESPONSE_2902为举例说明，具体根据底层给过来的文档去修改
                    if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                        if (mBluetoothLeService.setCharacteristicNotification(characteristic, true)) {
                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                                    UUID.fromString(BluetoothAttributes.UUID_NOTIFY));
                            descriptor.setValue(ENABLE_NOTIFICATION_VALUE);
                            mBluetoothLeService.writeDescriptor(descriptor);
                        }
                    }
                }
            }
        }
        //如果缓存特征服务为空，表示服务回调失败了，可以尝试断开连接或者关闭系统蓝牙重新去连接。
        if (mWriteHashMap.size() == 0) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null) bluetoothAdapter.disable();
            return;
        }
        Log.e("e", "蓝牙服务回调：mWriteHashMap=" + mWriteHashMap);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.e("e", "蓝牙数据读取回调 status=" + status);
        // TODO read
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        BluetoothDevice device = mBluetoothLeService.getDevice();//当前连接的蓝牙对象
        if (characteristic == null || characteristic.getValue() == null || device == null) return;
        String data = HexUtil.bytesToHexString(characteristic.getValue());//将byte类型数据转换为16进制
        Log.e("e", "蓝牙数据通知回调 uuid==" + characteristic.getUuid().toString() + ",data=" + data);
        //todo data
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.e("e", "蓝牙数据写入回调 status=" + status);
        if (status == BluetoothGatt.GATT_SUCCESS) {//数据写入成功，写入下一条指令
            //todo 写成功
        }
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        if (listener != null) listener.onReadRemoteRssi(gatt, rssi, status);
    }

}
