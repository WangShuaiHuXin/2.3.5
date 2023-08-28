package com.imapcloud.nest.v2.web.vo.resp;

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
public class DataAnalysisResultSumRespVO implements Serializable {
    @ApiModelProperty(value = "数量", position = 1,  example = "")
    private Integer total;
    @ApiModelProperty(value = "问题等级", position = 2,  example = "")
    private Integer problemLevel;



}
