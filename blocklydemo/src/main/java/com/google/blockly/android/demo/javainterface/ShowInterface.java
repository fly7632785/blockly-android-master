package com.google.blockly.android.demo.javainterface;

import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import com.google.blockly.android.demo.config.Config;
import com.google.blockly.android.demo.robot.CommandConstant;
import com.google.blockly.android.demo.robot.RobotBlocklyActivity;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * created by jafir on 2018/3/30
 */
public class ShowInterface extends Object {

    private RobotBlocklyActivity activity;
    private String returnStr;

    public ShowInterface(RobotBlocklyActivity activity) {
        this.activity = activity;
    }

    /**
     * 功能性结果 比如笑脸 黄色
     * @param msg
     */
    @JavascriptInterface
    public synchronized void command(String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        System.out.println("thread" + Thread.currentThread().toString());
        final String command = getOptionCommand(msg);
        if(command.equals("")){
            return;
        }
        if(command.equals("end")){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    activity.end();
                }
            });
            return;
        }
        try {
            Thread.sleep(Config.sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                activity.Write(command);
            }
        });
    }

    private String getOptionCommand(String msg) {
        switch (msg) {
            case "option_xiaolian":
                return CommandConstant.OPTION_XIAOLIAN;
            case "option_jvsang":
                return CommandConstant.OPTION_JVSANG;
            case "option_xiangshang":
                return CommandConstant.OPTION_XIANGSHANG;
            case "option_xiangxia":
                return CommandConstant.OPTION_XIANGXIA;
            case "option_xiangzuo":
                return CommandConstant.OPTION_XIANGZUO;
            case "option_xiangyou":
                return CommandConstant.OPTION_XIANGYOU;
            case "option_huangse":
                return CommandConstant.OPTION_HUANGSE;
            case "option_lanse":
                return CommandConstant.OPTION_LANSE;
            case "option_lvse":
                return CommandConstant.OPTION_LVSE;
            case "option_hongse":
                return CommandConstant.OPTION_HONGSE;
            case "option_zise":
                return CommandConstant.OPTION_ZISE;
            case "option_baise":
                return CommandConstant.OPTION_BAISE;
            case "option_fense":
                return CommandConstant.OPTION_FENSESE;
            case "option_close_rgb":
                return CommandConstant.OPTION_CLOSE_AGB;
            case "option_close_dianzhen":
                return CommandConstant.OPTION_CLOSE_DIANZHEN;
            case "option_end":
                return "end";
        }
        return "";
    }


    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public synchronized String sleep(String msg) {
        System.out.println("JS调用了Android的hello方法");
        System.out.println("thread" + Thread.currentThread().toString());
        long start = System.currentTimeMillis();
        System.out.println("wait");
        final AtomicBoolean flg = new AtomicBoolean(true);

        // 去操作蓝牙 发送和接受值
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("sleep 开始");
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        activity.Write("123");
                        activity.setListener(new RobotBlocklyActivity.ReceiverListener() {
                            @Override
                            public void onReceive(String s) {
                                returnStr = s;
                                System.out.println("哈哈:" + s);
                                flg.set(false);
                            }
                        });
                        System.out.println("thread:" + Thread.currentThread().toString());
                    }
                });
            }
        }).start();
        while (flg.get()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("wait....");
        }

        long end = System.currentTimeMillis();
        System.out.println("sleep 了" + (end - start) + "秒");
        return returnStr;
    }


}
