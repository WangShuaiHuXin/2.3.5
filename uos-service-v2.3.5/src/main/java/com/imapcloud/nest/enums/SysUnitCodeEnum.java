package com.imapcloud.nest.enums;

/**
 * 用户角色code
 * @author daolin
 */
public enum SysUnitCodeEnum {

    SUPER_ADMIN_UNIT(-1,"超管单位","0,1,2"),
    //父级单位，目前定义为中科云图，单位3
    PARENT_UNIT(3,"父级单位","0,1,2"),
    SUPER_ADMIN(0, "超级管理员","0,1,2"),
    ADMIN(1, "管理员","0,1,2"),
    NORMAL_USER(2, "普通用户","2"),
    CUSTOM_USER(3, "自定义角色","2");

    private Integer code;
    private String desc;
    private String access;

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

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    SysUnitCodeEnum(Integer code, String desc, String access) {
        this.code = code;
        this.desc = desc;
        this.access = access;
    }
}
