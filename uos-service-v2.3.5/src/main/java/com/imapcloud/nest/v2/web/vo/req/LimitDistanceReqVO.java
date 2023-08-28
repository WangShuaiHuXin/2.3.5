package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author wmin
 */
@Data
public class LimitDistanceReqVO {
    /**
     * 基站id
     */
    @NotNull
    private String nestId;

    /**
     * 需要限制的高度
     */
    @Min(15)
    @Max(8000)
    private Integer distance;
}
