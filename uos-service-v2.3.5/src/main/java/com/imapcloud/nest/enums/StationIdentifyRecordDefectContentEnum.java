package com.imapcloud.nest.enums;

import org.bouncycastle.LICENSE;

/**
 * 缺陷识别的具体内容
 * @author daolin
 */
public enum StationIdentifyRecordDefectContentEnum {

    
    GJBS("abnormal", "硅胶变色"),
    YWCL("yiwu", "异物处理"),
    HDPFW("rubbish","河道漂浮物"),
    NORMAL("normal", "正常硅胶"),
    PERSON("person", "人员"),
    GARBAGE("garbage", "垃圾堆"),
    BOAT("tiekechuan", "走私船"),
    DFC("dafei", ""),
    CAR("car","车辆"),
    ILLEGAL("illegal","违章建筑"),
    WILDFISHING("yediao","野钓");

    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    StationIdentifyRecordDefectContentEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
