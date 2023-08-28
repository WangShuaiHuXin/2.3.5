package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.common.annotation.NestId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Motor指令
 *
 * @author boluo
 * @date 2023-04-13
 */
@Data
public class CpsMotorReqVO {
    @NestId
    @NotBlank(message = "nestId is null")
    private String nestId;

    @NotNull(message = "which is null")
    private Integer which;
}
