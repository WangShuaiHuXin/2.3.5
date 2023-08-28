package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
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
public class DataAnalysisPicPageReqVO extends PageInfo implements Serializable {

    @ApiModelProperty(value = "分析中心基础表主键", position = 3, required = false, example = "")
    private String taskId;
    @ApiModelProperty(value = "架次记录ID", position = 4, required = false, example = "")
    private String missionRecordId;
    @ApiModelProperty(value = "照片状态", position = 5, required = false, example = "0-待分析，1-待确认有问题，2-待确认无问题，3-有问题，4-无问题")
    private Integer picStatus;

}