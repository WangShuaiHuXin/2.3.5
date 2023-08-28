package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@ApiModel("响应信息")
@EqualsAndHashCode(callSuper = false)
public class DataAnalysisPicTypeRespVO implements Serializable {
    @ApiModelProperty(value = "主键", position = 1, example = "")
    private List picTypeEnum;


}
