package com.imapcloud.nest.v2.common.enums;


public enum InspectionVerifyStateEnum {
    DAIHESHI("0", "待核实"),
    YIHESHI("1", "已核实"),
    WUBAO("2", "误报");
    private String type;
    private String value;

    InspectionVerifyStateEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public int getTypeInt() {
        return Integer.parseInt(type);
    }

    public String getValue() {
        return value;
    }

    public static String getValueByCode(String code) {
        InspectionVerifyStateEnum[] typesEnums = values();
        for (InspectionVerifyStateEnum typeenum : typesEnums) {
            if (typeenum.getType().equals(code)) {
                return typeenum.getValue();
            }
        }
        return null;
    }
}
