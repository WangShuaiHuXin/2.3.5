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
public class DataAnalysisCenterBaseReqVO implements Serializable {

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private String centerBaseId;

    @ApiModelProperty(value = "标签ID", position = 2, required = true, example = "10000")
    private String tagId;

    @ApiModelProperty(value = "任务ID", position = 3, required = true, example = "10000")
    private String taskId;

    @ApiModelProperty(value = "架次ID", position = 4, required = true, example = "10000")
    private String missionId;

    @ApiModelProperty(value = "架次记录ID", position = 5, required = true, example = "10000")
    private String missionRecordsId;

    @ApiModelProperty(value = "基站ID", position = 6, required = true, example = "10000")
    private String nestId;

    @ApiModelProperty(value = "单位ID", position = 7, required = true, example = "10000")
    private String orgId;

}
