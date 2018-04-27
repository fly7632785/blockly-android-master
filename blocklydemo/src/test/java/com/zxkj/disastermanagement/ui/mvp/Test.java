package com.zxkj.disastermanagement.ui.mvp;

import com.google.blockly.util.HexUtil;

/**
 * created by jafir on 2018/4/18
 */
public class Test {
    @org.junit.Test
    public static void main(String[]s){

        byte[]bb = "".getBytes();
        byte[] bbb = HexUtil.hexStringToBytes("D101FFFF");
        System.out.print(new String(bb));
        System.out.print(new String(bbb));

        byte[]bb1 = "D101FFFF韩建飞".getBytes();
        byte[] bbb1 = HexUtil.hexStringToBytes("D101FFFF韩建飞");
        System.out.print(new String(bb1));
        System.out.print(new String(bbb1));
    }
}
