package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 表计数据信息
 * @author Vastfy
 * @date 2022/11/29 10:59
 * @since 2.1.5
 */
@ApiModel("表计数据信息")
@Data
public class MeterDataRespVO implements Serializable {

    @ApiModelProperty(value = "表计数据ID", position = 1, example = "95279666")
    private String dataId;

    @ApiModelProperty(value = "飞行任务ID", position = 2, example = "9527")
    private String taskId;

    @ApiModelProperty(value = "飞行任务名称", position = 3, example = "日常巡检_表计0019")
    private String taskName;

    @ApiModelProperty(value = "飞行任务架次ID", position = 4, example = "123")
    private String missionId;

    @ApiModelProperty(value = "飞行任务架次顺序号", position = 5, example = "2")
    private String missionSeqId;

    @ApiModelProperty(value = "飞行任务架次ID", position = 6, example = "666777")
    private String missionRecordId;

    @ApiModelProperty(value = "架次任务飞行次数", position = 7, example = "4")
    private String flyIndex;

    @ApiModelProperty(value = "飞行任务架次飞行开始时间", position = 8, example = "2022-11-29 12:34:56")
    private LocalDateTime flightTime;

    @ApiModelProperty(value = "单位编码", position = 9, example = "000010")
    private String orgCode;



}
