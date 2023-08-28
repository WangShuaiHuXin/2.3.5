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
public class DataAnalysisQueryPicAllReqVO implements Serializable {

    @ApiModelProperty(value = "分析中心基础表主键", position = 3, required = false, example = "")
    private String centerBaseId;
    @ApiModelProperty(value = "照片状态", position = 4, required = false, example = "0-待分析，1-待确认有问题，2-待确认无问题，3-有问题，4-无问题")
    private Integer picStatus;
    @ApiModelProperty(value = "照片类型", position = 5, required = false, example = "")
    private Integer picType;

    @ApiModelProperty(value = "拍摄时间升降序", position = 7, required = false, example = "0-升序，1-降序")
    private int desc;
}