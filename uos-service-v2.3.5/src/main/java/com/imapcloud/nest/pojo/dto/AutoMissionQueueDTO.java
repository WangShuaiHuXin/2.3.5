package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.LimitVal;
import com.imapcloud.nest.common.annotation.NestId;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
public class AutoMissionQueueDTO {

    @NotNull
    @NestId
    private String nestId;

    @LimitVal(values = {"0", "1", "2"}, message = "gainDataMode只能是0、1、2")
    private Integer gainDataMode;

    @LimitVal(values = {"0", "1"}, message = "gainVideo只能是0、1")
    private Integer gainVideo;

    //只有G900需要
    private Integer positionStrategy = 1;

    @NotEmpty
    private List<Integer> missionIdList;

    private Integer planRecordId;
    /**
     * 计划是否是自动的
     */
    private Integer planAuto;

    private Long userId;

    private Integer planId;

    private String planName;

    private String account;

    /**
     * 网格化巡检记录ID
     */
    private String gridInspectId;

    /**
     * 海康威视定制化开发
     */
    private Integer flyType;
}
