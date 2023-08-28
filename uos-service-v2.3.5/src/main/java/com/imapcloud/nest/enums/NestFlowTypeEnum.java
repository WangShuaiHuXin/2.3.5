package com.imapcloud.nest.enums;

import com.alibaba.nacos.shaded.com.google.common.base.Objects;

/**
 * 流类型的枚举
 *
 * @author daolin
 */
public enum NestFlowTypeEnum {

    OUTSIDE_URL(0, "巢外监控"),
    INSIDE_URL(1, "巢内监控"),
    RTMP_URL(2, "图传地址"),
    UNKNOWN(-1, "位置");
    private Integer code;
    private String state;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    NestFlowTypeEnum(Integer code, String state) {
        this.code = code;
        this.state = state;
    }

    public static NestFlowTypeEnum getInstance(Integer code) {
        for (NestFlowTypeEnum e : NestFlowTypeEnum.values()) {
            if (Objects.equal(e.getCode(), code)) {
                return e;
            }
        }
        return UNKNOWN;
    }
}
