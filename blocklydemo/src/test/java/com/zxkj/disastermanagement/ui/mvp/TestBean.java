package com.zxkj.disastermanagement.ui.mvp;

/**
 * created by jafir on 2018/4/18
 */
public class TestBean {


    /**
     * status : 1
     * count : 217
     * info : OK
     * infocode : 10000
     */

    private String count;
    private String info;
    private String infocode;


    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    @Override
    public String toString() {
        return "TestBean{" +
                "count='" + count + '\'' +
                ", info='" + info + '\'' +
                ", infocode='" + infocode + '\'' +
                '}';
    }
}
