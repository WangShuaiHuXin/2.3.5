package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataAnalysisTaskPageReqVO extends PageInfo {

    @ApiModelProperty(value = "开始时间", position = 3, required = false, example = "")
    private String startTime;
    @ApiModelProperty(value = "结束时间", position = 4, required = false, example = "")
    private String endTime;
    @ApiModelProperty(value = "任务名", position = 5, required = false, example = "")
    private String taskName;
    @ApiModelProperty(value = "标签名", position = 6, required = false, example = "")
    private String tagName;
    @ApiModelProperty(value = "架次ID", position = 7, required = false, example = "")
    private String missionId;
    @ApiModelProperty(value = "架次记录ID", position = 8, required = false, example = "")
    private String missionRecordId;
    @ApiModelProperty(value = "基站ID", position = 9, required = false, example = "")
    private String nestId;
    @ApiModelProperty(value = "单位ID", position = 10, required = false, example = "")
    private String orgId;

}