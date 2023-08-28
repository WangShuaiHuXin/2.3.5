package com.imapcloud.sdk.pojo.entity;

/**
 * 云冠告警信息
 *
 * @author: zhengxd
 * @create: 2021/10/15
 **/
public class CloudCrownAlarmInfo {

//    {"distinguishResultX":805,"distinguishResultY":600,
//    "distinguishResultWidth":407,"distinguishResultHeight":274,
//    "distinguishResultOrdernum":1,"distinguishResultName":"laptop"}
        private Integer distinguishResultX;
        private Integer distinguishResultY;
        private Integer distinguishResultWidth;
        private Integer distinguishResultHeight;
        private Integer distinguishResultOrdernum;
        private String distinguishResultName;

    public Integer getDistinguishResultX() {
        return distinguishResultX;
    }

    public void setDistinguishResultX(Integer distinguishResultX) {
        this.distinguishResultX = distinguishResultX;
    }

    public Integer getDistinguishResultY() {
        return distinguishResultY;
    }

    public void setDistinguishResultY(Integer distinguishResultY) {
        this.distinguishResultY = distinguishResultY;
    }

    public Integer getDistinguishResultWidth() {
        return distinguishResultWidth;
    }

    public void setDistinguishResultWidth(Integer distinguishResultWidth) {
        this.distinguishResultWidth = distinguishResultWidth;
    }

    public Integer getDistinguishResultHeight() {
        return distinguishResultHeight;
    }

    public void setDistinguishResultHeight(Integer distinguishResultHeight) {
        this.distinguishResultHeight = distinguishResultHeight;
    }

    public Integer getDistinguishResultOrdernum() {
        return distinguishResultOrdernum;
    }

    public void setDistinguishResultOrdernum(Integer distinguishResultOrdernum) {
        this.distinguishResultOrdernum = distinguishResultOrdernum;
    }

    public String getDistinguishResultName() {
        return distinguishResultName;
    }

    public void setDistinguishResultName(String distinguishResultName) {
        this.distinguishResultName = distinguishResultName;
    }
}
