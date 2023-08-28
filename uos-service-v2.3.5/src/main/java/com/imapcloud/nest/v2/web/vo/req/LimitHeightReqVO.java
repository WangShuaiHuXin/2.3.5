package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
/**
 * @author wmin
 */
public class LimitHeightReqVO {

    /**
     * 基站id
     */
    @NotNull
    private String nestId;

    /**
     * 需要限制的高度
     */
    @Min(0)
    @Max(1500)
    private Integer height;
}
