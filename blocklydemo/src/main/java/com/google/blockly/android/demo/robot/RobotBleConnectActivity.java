package com.google.blockly.android.demo.robot;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.blockly.android.demo.R;
import com.google.blockly.android.demo.bleutils.BleController;
import com.google.blockly.android.demo.bleutils.callback.ConnectCallback;
import com.google.blockly.android.demo.bleutils.callback.ScanCallback;
import com.google.blockly.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * created by jafir on 2018/4/9
 */
public class RobotBleConnectActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private List devices = new ArrayList();
    private DeviceAdapter adapter;
    private BleController mBleController;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        setContentView(R.layout.activity_connect);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");
        checkGps();
        mBleController = BleController.getInstance().initble(this);
        initReycler();
        scanDevices(true);
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        new MenuInflater(this).inflate(R.menu.refresh, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//            return true;
//        } else if (item.getItemId() == R.id.action_refresh) {
//
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    @OnClick(R.id.refresh)
    public void onRefresh() {
        adapter.getData().clear();
        adapter.notifyDataSetChanged();
        if (mBleController.isScanning()) {
            scanDevices(false);
        }
        scanDevices(true);
    }

    @OnClick(R.id.back)
    public void onback() {
        finish();
    }

    /**
     * 开启位置权限
     */
    private void checkGps() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanDevices(true);
                Toast.makeText(this, "位置权限已开启", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "未开启位置权限", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initReycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeviceAdapter(R.layout.item_device, devices);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final BluetoothDevice device = (BluetoothDevice) adapter.getData().get(position);
                if (mBleController.isConnected()) {
                    ToastUtils.show("已连接");
                    return;
                }
                showProgressDialog("请稍候!", "正在连接...");
                if (device == null)
                    return;
                Log.e("e", device.getAddress());

                mBleController.Connect(device.getAddress(), new ConnectCallback() {
                    @Override
                    public void onConnSuccess() {
                        hideProgressDialog();
                        //todo
                        Toast.makeText(RobotBleConnectActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
//                        finish();
                    }

                    @Override
                    public void onConnFailed() {
                        hideProgressDialog();
                        Toast.makeText(RobotBleConnectActivity.this, "连接断开或连接失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    public void showProgressDialog(String title, String message) {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, title, message, true, true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mBleController.disConnectBleConn();
                }
            });
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
     * 扫描
     *
     * @param enable
     */
    private void scanDevices(final boolean enable) {
        text.setText("正在搜索中...");
        mBleController.ScanBle(enable, new ScanCallback() {
            @Override
            public void onSuccess() {
                if (!adapter.getData().isEmpty()) {
                    text.setText("没有发现设备");
                } else if (mBleController.isConnected()) {
                    text.setText("设备已连接");
                } else {
                    text.setText("正在搜索中...");
                }
            }

            @Override
            public void onScanning(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (!adapter.getData().contains(device)) {
                    adapter.getData().add(device);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, RobotBleConnectActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanDevices(false);
    }
}
