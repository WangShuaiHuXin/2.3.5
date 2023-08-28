package com.imapcloud.nest.enums;

/**
 * 菜单级别等级
 * @author daolin
 */
public enum SysMenuLevelEnum {

    FIRST(0, "一级菜单"),
    SECOND(1, "二级菜单"),
    THIRD(2, "三级菜单");
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

    SysMenuLevelEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
