package com.google.blockly.android.demo.robot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.blockly.android.demo.R;
import com.google.blockly.android.demo.bleutils.BleController;
import com.google.blockly.android.demo.bleutils.callback.OnReceiverCallback;
import com.google.blockly.android.demo.bleutils.callback.OnWriteCallback;
import com.google.blockly.util.HexUtil;
import com.google.blockly.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * created by jafir on 2018/4/6
 */
public class RobotControlActivity extends AppCompatActivity {
    private static final String TAG = "RobotControlActivity";
    private BleController mBleController;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, RobotControlActivity.class));
    }

    @BindView(R.id.img)
    ImageView imageView;
    @BindView(R.id.layout_control)
    View controlLayout;

    @BindView(R.id.start_and_pause)
    ImageView startAndPause;

    @BindView(R.id.introduce)
    TextView introduce;

    @BindView(R.id.control)
    View control;
    @BindView(R.id.follow)
    View xunji;
    @BindView(R.id.hide)
    View hide;
    @BindView(R.id.prevent_down)
    View prevent;


    enum Mode {
        Control,//控制
        Follow,//循迹
        Hide,//避障
        Prevent//防跌落
    }

    Mode mode = Mode.Control;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        setContentView(R.layout.activity_control);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mBleController = BleController.getInstance();
        mBleController.RegistReciveListener(TAG, new OnReceiverCallback() {
            @Override
            public void onReceiver(byte[] value) {
                Log.e("response", new String(value));
                Log.e("response", HexUtil.bytesToHexString(value));
            }
        });
        control.setSelected(true);
    }

    @OnClick(R.id.to_back)
    public void back() {
        finish();
    }

    @OnClick({R.id.control, R.id.follow, R.id.hide, R.id.prevent_down})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.control:
                control(Mode.Control);
                break;
            case R.id.follow:
                control(Mode.Follow);
                break;
            case R.id.hide:
                control(Mode.Hide);
                break;
            case R.id.prevent_down:
                control(Mode.Prevent);
                break;
        }
    }

    private void control(Mode mode) {
        if (this.mode != mode && startAndPause.isSelected()) {
            ToastUtils.show("请先停止，再切换其他选项");
            return;
        }
        this.mode = mode;
        controlLayout.setVisibility(View.GONE);
        control.setSelected(false);
        xunji.setSelected(false);
        hide.setSelected(false);
        prevent.setSelected(false);
        switch (mode) {
            case Control:
                control.setSelected(true);
                imageView.setVisibility(View.GONE);
                introduce.setVisibility(View.GONE);
                controlLayout.setVisibility(View.VISIBLE);
                startAndPause.setImageResource(R.drawable.select_start_pause_yellow);
                break;
            case Follow:
                xunji.setSelected(true);
                imageView.setImageResource(R.mipmap.xunji);
                imageView.setVisibility(View.VISIBLE);
                introduce.setVisibility(View.VISIBLE);
                introduce.setText("循迹");
                startAndPause.setImageResource(R.drawable.select_start_pause_blue);
                break;
            case Hide:
                hide.setSelected(true);
                imageView.setImageResource(R.mipmap.bizhang);
                imageView.setVisibility(View.VISIBLE);
                introduce.setVisibility(View.VISIBLE);
                introduce.setText("避障");
                startAndPause.setImageResource(R.drawable.select_start_pause_red);
                break;
            case Prevent:
                prevent.setSelected(true);
                imageView.setImageResource(R.mipmap.fangdieluo);
                imageView.setVisibility(View.VISIBLE);
                introduce.setVisibility(View.VISIBLE);
                introduce.setText("防跌落");
                startAndPause.setImageResource(R.drawable.select_start_pause_green);
                break;
        }

    }

    @OnClick(R.id.start_and_pause)
    public void startAndPause(View view) {
        //selected   true 为暂停  false为开始
        view.setSelected(!view.isSelected());
        if (view.isSelected()) {
            //开始
            switch (mode) {
                case Follow:
                    //todo 循迹
                    sendCommand(CommandConstant.XUNJI_START);
                    break;
                case Hide:
                    //todo 避障
                    sendCommand(CommandConstant.BIZHANG_START);
                    break;
                case Prevent:
                    sendCommand(CommandConstant.FANGDIELUO_START);
                    //todo 防跌落
                    break;
            }
        } else {
            //暂停
            switch (mode) {
                case Follow:
                    //todo 循迹
                    sendCommand(CommandConstant.XUNJI_END);
                    break;
                case Hide:
                    //todo 避障
                    sendCommand(CommandConstant.BIZHANG_END);
                    break;
                case Prevent:
                    sendCommand(CommandConstant.FANGDIELUO_END);
                    //todo 防跌落
                    break;
            }
        }
    }

    @SuppressLint("CheckResult")
    private void sendCommand(String command) {
        if (mBleController.isConnected()) {
            mBleController.WriteBuffer(command, new OnWriteCallback() {
                @Override
                public void onSuccess() {
                    Log.e("debug", "onSuccess");
                }

                @Override
                public void onFailed(int state) {
                    ToastUtils.show("发送失败");
                    Log.e("debug", "onFailed" + state);
                }
            });
        } else {
            ToastUtils.show("已断开连接，请重新连接蓝牙设备");
        }
        //设置 notify
//
//        Observable<RxBleConnection> connect =  MainApplication.getInstance().connectObserverable;
//        connect = MainApplication.getInstance().bleDevice.establishConnection(false);
//        connect.flatMap(rxBleConnection -> rxBleConnection.setupNotification(BleUUID.UUID_NOTIFY))
//                .flatMap(notificationObservable -> notificationObservable) // <-- Notification has been set up, now observe value changes.
//                .subscribe(
//                        bytes -> {
//                            // Given characteristic has been changes, here is the value.
//                            Log.e("e", "返回" + HexUtil.bytesToHexString(bytes));
//                        },
//                        throwable -> {
//                            // Handle an error here.
//                            Log.e("e", "throwable" + throwable.getMessage());
//                        }
//                );
//        connect
//                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(BleUUID.UUID_WRITE, HexUtil.hexStringToBytes(command)))
//                .subscribe(
//                        characteristicValue -> {
//                            // Characteristic value confirmed.
//                            Log.e("e", "写成功" + HexUtil.bytesToHexString(characteristicValue));
//                        },
//                        throwable -> {
//                            Log.e("e", "throwable" + throwable.getMessage());
//                        }
//                );
    }


    @OnClick({R.id.go, R.id.left, R.id.stop, R.id.right, R.id.back})
    public void contorllorClick(View v) {
        switch (v.getId()) {
            case R.id.go:
                //todo 前进
                sendCommand(CommandConstant.GO);
                break;
            case R.id.left:
                sendCommand(CommandConstant.LEFT);
                //todo 向左
                break;
            case R.id.right:
                sendCommand(CommandConstant.RIGHT);
                //todo 向右
                break;
            case R.id.stop:
                sendCommand(CommandConstant.STOP);
                //todo 停止
                break;
            case R.id.back:
                sendCommand(CommandConstant.BACK);
                //todo 后退
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBleController.UnregistReciveListener(TAG);
    }
}
