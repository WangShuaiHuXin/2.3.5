package com.imapcloud.nest.enums;

/**
 * 菜单级别等级
 * @author daolin
 */
public enum TaskMoldEnum {

    NEST(0, "机巢航线"),
    //查出是移动终端的，因为移动终端不可以直接展示出来
    APP(1, "移动终端航线");
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

    TaskMoldEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
