package com.imapcloud.nest.enums;

/**
 *
 */
public enum PhotoTypeEnum {
    VISIBLE(0, "可见光照片","VIS"),
    INFRARED(1, "红外光照片","INFRA"),
    VISIBLE_INFRARED(2, "红外可见合成","VIS_INFRA"),
    THRM(3, "M300红外","THRM"),
    WIDE(4, "M300广角","WIDE"),
    ZOOM(5, "M300变焦","ZOOM");
    private int value;
    private String express;
    private String code;

    PhotoTypeEnum(int value, String express,String code) {
        this.value = value;
        this.express = express;
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getExpress() {
        return express;
    }

    public void setExpress(String express) {
        this.express = express;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static String getCodeByValue(int value){
        //values() 会获取定义的 enum对象
        for(PhotoTypeEnum item : values()){
            if(item.getValue()==value){
                return item.getCode();
            }
        }
        return null;
    }
}
