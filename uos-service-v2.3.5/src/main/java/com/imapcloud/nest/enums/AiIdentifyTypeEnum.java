package com.imapcloud.nest.enums;

/**
 * AI识别类型
 *
 * @Author: wmin
 * @Date: 2021/4/6 17:14
 */
public enum AiIdentifyTypeEnum {
    /**
     * AI识别类型
     */
    DEFECT_IDENTIFICATION(0, "缺陷识别"),
    METER_READ(1, "表计读数"),
    INFRARED_THERMOMETRY(2, "红外测温"),
    RIVER_INSPECTION(3, "河道巡查"),
    TRAFFIC_INSPECTION(4, "交通巡查"),
    FIXED_POINT_EVIDENCE(5, "定点取证");
    private int val;
    private String express;

    AiIdentifyTypeEnum(int val, String express) {
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
