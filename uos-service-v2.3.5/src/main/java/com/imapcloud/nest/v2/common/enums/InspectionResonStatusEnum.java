package com.imapcloud.nest.v2.common.enums;

public enum  InspectionResonStatusEnum {

    /**
     * 存在读数异常
     */
    AbnormalReadings("0", "存在读数异常"),
    /**
     * 未能识别读数
     */
    FailureToRecognizeReadings	("1", "未能识别读数"),
    /**
     * 未设置读数项
     */
    NoReadingItemset("2", "未设置读数项"),


    ;
    private String code;
    private String value;

    InspectionResonStatusEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static String getValueByCode(String code) {
        InspectionResonStatusEnum[] typesEnums = values();
        for (InspectionResonStatusEnum typeenum : typesEnums) {
            if (typeenum.getCode().equals(code)) {
                return typeenum.getValue();
            }
        }
        return null;
    }
}
