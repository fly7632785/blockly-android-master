package com.google.blockly.android.demo.javainterface;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.blockly.android.demo.config.Config;
import com.google.blockly.android.demo.robot.CommandConstant;
import com.google.blockly.android.demo.robot.RobotBlocklyActivity;


/**
 * created by jafir on 2018/3/30
 */
public class MoveInterface extends Object {


    private RobotBlocklyActivity activity;
    private String returnStr;
    private long lastTime;

    public MoveInterface(RobotBlocklyActivity activity) {
        this.activity = activity;
    }

    /**
     * 前进后退  速度 时间
     */
    @JavascriptInterface
    public synchronized void goBackTime(final int goBack, final int speed, final int time) {
        try {
            Log.e("pause", String.valueOf(System.currentTimeMillis() - lastTime));
            Thread.sleep(Config.sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            lastTime = System.currentTimeMillis();
            String command = String.format(CommandConstant.GOBACKTIME, goBack, getFormatSpeed(speed), getFormatTime(time));
            Log.e("command", command);
            activity.Write(command);
        });
    }

    /**
     * 前进后退  速度
     */
    @JavascriptInterface
    public synchronized void goBack(final int goBack, final int speed) {
        try {
            Thread.sleep(Config.sleepTime);
            Log.e("pause", String.valueOf(System.currentTimeMillis() - lastTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            lastTime = System.currentTimeMillis();
            String command = String.format(CommandConstant.GOBACK, goBack, getFormatSpeed(speed));
            Log.e("command", command);
            activity.Write(command);
        });
    }

    /**
     * 顺时针逆时针  速度 时间
     */
    @JavascriptInterface
    public synchronized void clockwise(final int clockwise, final int speed, final int time) {
        try {
            Thread.sleep(Config.sleepTime);
            Log.e("pause", String.valueOf(System.currentTimeMillis() - lastTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            lastTime = System.currentTimeMillis();
            String command = String.format(CommandConstant.CLOCKWISE, clockwise, getFormatSpeed(speed), getFormatTime(time));
            Log.e("command", command);
            activity.Write(command);
        });
    }

    private String getFormatSpeed(int speed) {
        int speedHex = speed;
        if (speedHex > 255) {
            speedHex = 255;
        }
        if (speedHex < 100) {
            speedHex = 100;
        }
        String string = Integer.toHexString(speedHex).toUpperCase();
        if (string.length() == 1) {
            string = "0" + string;
        }
        return string;
    }

    /**
     * 01 - 255
     * 01 - FF
     *
     * @param time
     * @return
     */
    private String getFormatTime(int time) {
        String string = Integer.toHexString(time).toUpperCase();
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
        try {
            Thread.sleep(Config.sleepTime);
            Log.e("pause", String.valueOf((System.currentTimeMillis() - lastTime)));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            lastTime = System.currentTimeMillis();
            String command = String.format(CommandConstant.ZHUANWAN, clockwise, getFormatSpeed(speed));
            Log.e("command", command);
            activity.Write(command);
        });
    }

    /**
     * 停止运动
     */
    @JavascriptInterface
    public synchronized void stop() {
        try {
            Thread.sleep(Config.sleepTime);
            Log.e("pause", String.valueOf(System.currentTimeMillis() - lastTime));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            lastTime = System.currentTimeMillis();
            activity.Write(CommandConstant.MOVE_STOP);
        });
    }

}
