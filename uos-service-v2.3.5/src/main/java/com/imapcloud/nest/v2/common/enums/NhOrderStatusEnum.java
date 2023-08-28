package com.imapcloud.nest.v2.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NhOrderStatusEnum {

    To_Be_Publish(1, "待发布"),
    To_Be_Signed(2, "待签收"),
    Declined(3, "已拒收"),
    To_Be_Reviewed(4, "待提审"),
    Be_Reviewed(5, "待审核"),
    Does_Not_Pass(6, "不通过"),
    Accepted(7, "已验收");
    private Integer status;

    private String desc;

    public String getDesc() {
        return desc;
    }

    public Integer getStatus() {
        return status;
    }
}
