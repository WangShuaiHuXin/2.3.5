package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class PowerEquipmentMatchReqVO implements Serializable {
    @NotNull(message = "单位编码不能为空")
    private String orgCode;

    @NotNull(message = "匹配类型不能为空")
    private String col;
}
