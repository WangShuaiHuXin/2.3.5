package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataAnalysisMarkAddrReqVO implements Serializable {

    @ApiModelProperty(value = "标注主键", position = 1, required = false, example = "")
    private String markId;

    @ApiModelProperty(value = "经度", position = 4, required = false, example = "")
    @NotNull(message = "经度 不能为空")
    private BigDecimal longitude;
    @ApiModelProperty(value = "纬度", position = 5, required = false, example = "")
    @NotNull(message = "纬度 不能为空")
    private BigDecimal latitude;

}
