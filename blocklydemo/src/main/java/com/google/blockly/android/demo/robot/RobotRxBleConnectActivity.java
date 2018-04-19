package com.google.blockly.android.demo.robot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.blockly.MainApplication;
import com.google.blockly.android.demo.R;
import com.google.blockly.util.ToastUtils;
import com.jakewharton.rx.ReplayingShare;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.exceptions.BleScanException;
import com.polidea.rxandroidble2.scan.ScanFilter;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

import static com.trello.rxlifecycle2.android.ActivityEvent.PAUSE;

/**
 * created by jafir on 2018/4/9
 */
public class RobotRxBleConnectActivity extends RxAppCompatActivity {


    private static final int REQUEST_ENABLE_BT = 2;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    private RxDeviceAdapter adapter;
    private ProgressDialog progressDialog;
    private RxBleClient rxBleClient;
    private Disposable scanDisposable;
    private RxBleDevice bleDevice;
    private PublishSubject<Boolean> disconnectTriggerSubject = PublishSubject.create();
    private Observable<RxBleConnection> connectionObservable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initReycler();
        rxBleClient = MainApplication.getRxBleClient(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_refresh) {
            scanDevices();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initReycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RxDeviceAdapter(R.layout.item_device);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final ScanResult device = (ScanResult) adapter.getData().get(position);
                if (device == null)
                    return;
                Log.e("e", device.getBleDevice().getMacAddress());
                MainApplication.address = device.getBleDevice().getMacAddress();
                connect(device.getBleDevice().getMacAddress());
            }
        });
    }


    private Observable<RxBleConnection> prepareConnectionObservable() {
        return bleDevice
                .establishConnection(false)
                .takeUntil(disconnectTriggerSubject)
                .compose(bindUntilEvent(PAUSE))
                .compose(ReplayingShare.instance());
    }

    @SuppressLint("CheckResult")
    private void connect(String address) {
        bleDevice = MainApplication.getRxBleClient(this).getBleDevice(address);
        connectionObservable = prepareConnectionObservable();
        MainApplication.getInstance().connectObserverable = connectionObservable;
        MainApplication.getInstance().bleDevice = bleDevice;
        if (isConnected()) {
            triggerDisconnect();
        } else {
            connectionObservable
                    .flatMapSingle(RxBleConnection::discoverServices)
                    .flatMapSingle(rxBleDeviceServices -> rxBleDeviceServices.getCharacteristic(BleUUID.UUID_NOTIFY))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> showProgressDialog("请稍候!", "正在连接..."))
                    .subscribe(
                            characteristic -> {
                                hideProgressDialog();
                                if (characteristic != null) {
                                    ToastUtils.show("连接成功");
                                    Log.i(getClass().getSimpleName(), "连接成功");
                                }
                            },
                            this::onConnectionFailure,
                            this::onConnectionFinished
                    );
        }
    }


    private void onNotificationReceived(byte[] bytes) {
    }

    private void onNotificationSetupFailure(Throwable throwable) {
    }


    private void onConnectionFailure(Throwable throwable) {
        hideProgressDialog();
        //noinspection ConstantConditions
//        Snackbar.make(findViewById(R.id.main), "Connection error: " + throwable, Snackbar.LENGTH_SHORT).show();
    }

    private void onConnectionFinished() {
        hideProgressDialog();
    }

    private void triggerDisconnect() {
        disconnectTriggerSubject.onNext(true);
    }

    private boolean isConnected() {
        return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    public void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, title, message, true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    /**
     * ************************************************** 扫描*************************************************
     *
     * @return
     */


    private boolean isScanning() {
        return scanDisposable != null;
    }

    /**
     * 扫描
     */
    private void scanDevices() {
        if (isScanning())
            scanDisposable.dispose();
        scanDisposable = rxBleClient.scanBleDevices(
                new ScanSettings.Builder()
//                            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                        .build(),
                new ScanFilter.Builder()
//                            .setDeviceAddress("B4:99:4C:34:DC:8B")
                        // add custom filters if needed
                        .build()
        )
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::dispose)
//                    .throttleFirst(300,TimeUnit.MILLISECONDS)
                .subscribe(device -> {
                    for (int i = 0; i < adapter.getData().size(); i++) {
                        if (adapter.getData().get(i).getBleDevice().getMacAddress().equals(device.getBleDevice().getMacAddress())) {
                            return;
                        }
                    }
                    adapter.addData(device);
                }, this::onScanFailure);
    }

    private void onScanFailure(Throwable throwable) {
        if (throwable instanceof BleScanException) {
            handleBleScanException((BleScanException) throwable);
        }
    }

    private void handleBleScanException(BleScanException bleScanException) {
        final String text;

        switch (bleScanException.getReason()) {
            case BleScanException.BLUETOOTH_NOT_AVAILABLE:
                text = "Bluetooth is not available";
                break;
            case BleScanException.BLUETOOTH_DISABLED:
                if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                }
                text = "Enable bluetooth and try again";
                break;
            case BleScanException.LOCATION_PERMISSION_MISSING:
                text = "";
                new RxPermissions(this)
                        .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                        .subscribe(aBoolean -> {
                            if (!aBoolean) {
//                                ScanActivity.this.finish();
                                String toast = "On Android 6.0 location permission is required. Implement Runtime Permissions";
                                Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
                            }
                        }, e -> Log.e("e", e.getMessage()));
                break;
            case BleScanException.LOCATION_SERVICES_DISABLED:
                text = "Location services needs to be enabled on Android 6.0";
                break;
            case BleScanException.SCAN_FAILED_ALREADY_STARTED:
                text = "Scan with the same filters is already started";
                break;
            case BleScanException.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                text = "Failed to register application for bluetooth scan";
                break;
            case BleScanException.SCAN_FAILED_FEATURE_UNSUPPORTED:
                text = "Scan with specified parameters is not supported";
                break;
            case BleScanException.SCAN_FAILED_INTERNAL_ERROR:
                text = "Scan failed due to internal error";
                break;
            case BleScanException.SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES:
                text = "Scan cannot start due to limited hardware resources";
                break;
            case BleScanException.UNDOCUMENTED_SCAN_THROTTLE:
                text = String.format(
                        Locale.getDefault(),
                        "Android 7+ does not allow more scans. Try in %d seconds",
                        secondsTill(bleScanException.getRetryDateSuggestion())
                );
                break;
            case BleScanException.UNKNOWN_ERROR_CODE:
            case BleScanException.BLUETOOTH_CANNOT_START:
            default:
                text = "Unable to start scanning";
                break;
        }
        Log.w("EXCEPTION", text, bleScanException);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private long secondsTill(Date retryDateSuggestion) {
        return TimeUnit.MILLISECONDS.toSeconds(retryDateSuggestion.getTime() - System.currentTimeMillis());
    }

    private void dispose() {
        scanDisposable = null;
        adapter.getData().clear();
        adapter.notifyDataSetChanged();
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, RobotRxBleConnectActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanDevices();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isScanning()) {
            /*
             * Stop scanning in onPause callback. You can use rxlifecycle for convenience. Examples are provided later.
             */
            scanDisposable.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        triggerDisconnect();
    }
}
