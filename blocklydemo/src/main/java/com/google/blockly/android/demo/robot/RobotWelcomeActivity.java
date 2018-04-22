package com.google.blockly.android.demo.robot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.google.blockly.android.demo.R;
import com.google.blockly.android.demo.bleutils.BleController;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * created by jafir on 2018/4/6
 */
public class RobotWelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
    }

    @OnClick({R.id.control, R.id.create, R.id.bluetooth})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.control:
//                if(BleController.getInstance().isConnected()) {
                RobotControlActivity.launch(getApplicationContext());
//                }else {
//                    Toast.makeText(this, "请连接蓝牙", Toast.LENGTH_SHORT).show();
//                }
                break;
            case R.id.create:
//                if(BleController.getInstance().isConnected()) {
                RobotBlocklyActivity.launch(getApplicationContext());
//                }else {
//                    Toast.makeText(this, "请连接蓝牙", Toast.LENGTH_SHORT).show();
//                }
                break;
            case R.id.bluetooth:
                RobotBleConnectActivity.launch(this);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO 断开连接
        BleController.getInstance().CloseBleConn();
    }
}
