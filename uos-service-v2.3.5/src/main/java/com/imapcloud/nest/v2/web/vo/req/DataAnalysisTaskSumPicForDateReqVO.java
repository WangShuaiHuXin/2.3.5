package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataAnalysisTaskSumPicForDateReqVO implements Serializable {

    @ApiModelProperty(value = "主键", position = 1, required = false, example = "")
    private String centerBaseId;
    @ApiModelProperty(value = "架次ID", position = 2, required = false, example = "")
    private String missionId;
    @ApiModelProperty(value = "架次记录ID", position = 3, required = false, example = "")
    private String missionRecordId;
    @ApiModelProperty(value = "组织ID", position = 4, required = false, example = "")
    private String orgId;
    @ApiModelProperty(value = "开始时间", position = 5, required = false, example = "")
    private String startTime;
    @ApiModelProperty(value = "结束时间", position = 6, required = false, example = "")
    private String endTime;

}