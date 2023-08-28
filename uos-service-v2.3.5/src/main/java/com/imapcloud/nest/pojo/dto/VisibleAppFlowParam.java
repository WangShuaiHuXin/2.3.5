package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * @author wmin
 */
@Data
public class VisibleAppFlowParam {
    @NotNull
    private Integer state;
    private String unitId;
    private String appId;
}
