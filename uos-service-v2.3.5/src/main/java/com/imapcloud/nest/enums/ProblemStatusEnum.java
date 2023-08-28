package com.imapcloud.nest.enums;

/**
 * 菜单级别等级
 * @author daolin
 */
public enum ProblemStatusEnum {
    UN_DEFECT(0, "未识别"),
    NO_DEFECT(1, "无问题"),
    DEFECT(2, "有问题"),
    DEFECT_REMOVED(3, "已解决");
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

    ProblemStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
