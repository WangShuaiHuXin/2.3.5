package com.imapcloud.nest.enums;

/**
 * 流媒体类型
 * 类型，0-推，1-拉，2-中间转发，3-第三方
 *
 * @Author: daolin
 * @Date: 2022/12/9 13:14
 */
public enum MediaServiceTypeEnum {

    MEDIA_PUSH(0, "推流服务器"),
    MEDIA_PULL(1, "拉流服务器"),
    MEDIA_RELAY(2, "转发服务器"),
    media_third(3, "第三方流媒体"),;
    private int val;
    private String express;

    MediaServiceTypeEnum(int val, String express) {
        this.val = val;
        this.express = express;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }


}
