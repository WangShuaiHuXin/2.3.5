package com.imapcloud.nest.enums;

/**
 * 用户是否删除的状态
 * @author daolin
 */
public enum SysFunctionTypeEnum {

    TYPE_TASK(0, "批量任务管理"),
    TYPE_MENU(1, "菜单任务列表");
    private Integer code;
    private String desc;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    SysFunctionTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
