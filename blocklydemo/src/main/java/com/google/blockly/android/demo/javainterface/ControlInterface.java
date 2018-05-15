package com.google.blockly.android.demo.javainterface;

import android.webkit.JavascriptInterface;

import com.google.blockly.android.demo.robot.RobotBlocklyActivity;


/**
 * created by jafir on 2018/3/30
 */
public class ControlInterface extends Object {


    private RobotBlocklyActivity activity;
    private String returnStr;

    public ControlInterface(RobotBlocklyActivity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public synchronized void wait(final int time) {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
