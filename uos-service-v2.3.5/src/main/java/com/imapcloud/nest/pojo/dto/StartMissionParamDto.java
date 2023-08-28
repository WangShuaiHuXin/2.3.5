package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.LimitVal;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wmin
 */
@Data
public class StartMissionParamDto {
    @NotNull
    private Integer missionId;
    @LimitVal(values = {"0", "1", "2"}, message = "gainDataMode只能是0、1、2")
    private Integer gainDataMode;
    @LimitVal(values = {"0", "1"}, message = "gainVideo只能是0、1")
    private Integer gainVideo;
    @LimitVal(values = {"1", "2"}, message = "positionStrategy只能是1、2")
    private Integer positionStrategy;

    private boolean multiTask;

    /**
     * 类似G503的机位
     */
    private Integer uavWhich;

    /**
     * 网格化巡检记录ID
     */
    private String gridInspectId;

    /**
     * 账号ID
     * @since 2.2.5
     */
    private String accountId;

    /**
     * 海康威视定制化开发
     */
    private Integer flyType;
}
