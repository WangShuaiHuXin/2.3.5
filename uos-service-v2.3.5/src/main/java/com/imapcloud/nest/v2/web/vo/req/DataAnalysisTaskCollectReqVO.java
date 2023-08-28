package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisTaskCollectReqVO.java
 * @Description DataAnalysisTaskCollectReqVO
 * @createTime 2022年07月08日 17:56:00
 */
@Data
public class DataAnalysisTaskCollectReqVO implements Serializable {
    @ApiModelProperty(value = "任务ID", position = 1, required = true, example = "10000")
    private String taskId;
}
