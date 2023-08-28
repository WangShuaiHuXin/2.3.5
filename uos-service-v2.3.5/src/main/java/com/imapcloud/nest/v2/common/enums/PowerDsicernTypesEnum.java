package com.imapcloud.nest.v2.common.enums;


public enum PowerDsicernTypesEnum {
    BIAOJI("101", "表计读数"),
    QUEXIAN("102", "外观检测"),
    HONGWAI("103", "红外测温");
    private String type;
    private String value;

    PowerDsicernTypesEnum(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static String getValueByCode(String code) {
        PowerDsicernTypesEnum[] typesEnums = values();
        for (PowerDsicernTypesEnum typeenum : typesEnums) {
            if (typeenum.getType().equals(code)) {
                return typeenum.getValue();
            }
        }
        return null;
    }
}
