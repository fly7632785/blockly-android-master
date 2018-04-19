package com.google.blockly.android.demo.javainterface;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.blockly.android.demo.robot.CommandConstant;
import com.google.blockly.android.demo.robot.RobotBlocklyActivity;


/**
 * created by jafir on 2018/3/30
 */
public class MoveInterface extends Object {


    private RobotBlocklyActivity activity;
    private String returnStr;

    public MoveInterface(RobotBlocklyActivity activity) {
        this.activity = activity;
    }

    /**
     * 前进后退  速度 时间
     */
    @JavascriptInterface
    public synchronized void goBackTime(final int goBack, final int speed, final int time) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String command = String.format(CommandConstant.GOBACKTIME, goBack, getFormatSpeed(speed), time);
                Log.e("command", command);
                activity.Write(command);
            }
        });
    }

    /**
     * 前进后退  速度
     */
    @JavascriptInterface
    public synchronized void goBack(final int goBack, final int speed) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String command = String.format(CommandConstant.GOBACK, goBack, getFormatSpeed(speed));
                Log.e("command", command);
                activity.Write(command);
            }
        });
    }

    /**
     * 顺时针逆时针  速度 时间
     */
    @JavascriptInterface
    public synchronized void clockwise(final int clockwise, final int speed, final int time) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String command = String.format(CommandConstant.CLOCKWISE, clockwise, getFormatSpeed(speed), time);
                Log.e("command", command);
                activity.Write(command);
            }
        });
    }

    private String getFormatSpeed(int speed) {
        int speedHex = (int) (speed * 2.55);
        String string = Integer.toHexString(speedHex).toUpperCase();
        if (string.length() == 1) {
            string = "0" + string;
        }
        return string;
    }

    /**
     * 顺时针逆时针  速度
     */
    @JavascriptInterface
    public synchronized void direction(final int clockwise, final int speed) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String command = String.format(CommandConstant.ZHUANWAN, clockwise, getFormatSpeed(speed));
                Log.e("command", command);
                activity.Write(command);
            }
        });
    }

    /**
     * 停止运动
     */
    @JavascriptInterface
    public synchronized void stop() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.e("command", CommandConstant.STOP);
                activity.Write(CommandConstant.STOP);
            }
        });
    }

}
