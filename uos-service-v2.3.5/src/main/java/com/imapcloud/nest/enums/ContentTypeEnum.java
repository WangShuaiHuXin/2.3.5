package com.imapcloud.nest.enums;

public enum ContentTypeEnum {

    XLS("xls", "application/vnd.ms-excel"),

    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private String name;
    private String type;

    ContentTypeEnum(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }
}
