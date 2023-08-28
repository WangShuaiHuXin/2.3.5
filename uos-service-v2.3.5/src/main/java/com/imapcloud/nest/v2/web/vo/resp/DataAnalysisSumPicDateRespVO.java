package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
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
@ApiModel("响应信息")
@EqualsAndHashCode(callSuper = false)
public class DataAnalysisSumPicDateRespVO implements Serializable {
    @ApiModelProperty(value = "主键", position = 1, example = "")
    private String centerBaseId;
    @ApiModelProperty(value = "架次记录ID", position = 2, example = "")
    private String missionRecordId;
    @ApiModelProperty(value = "单位ID", position = 3, example = "")
    private String orgId;
//    @ApiModelProperty(value = "待分析数", position = 4, example = "")
//    private Integer needAnalyzeSum;
//    @ApiModelProperty(value = "待确认有问题数", position = 5, example = "")
//    private Integer needConfirmProblemSum;
//    @ApiModelProperty(value = "待确认无问题数", position = 6, example = "")
//    private Integer needConfirmNoProblemSum;
//    @ApiModelProperty(value = "有问题数", position = 7, example = "")
//    private Integer problemSum;
//    @ApiModelProperty(value = "无问题数", position = 8, example = "")
//    private Integer noProblemSum;
    @ApiModelProperty(value = "已分析数", position = 4, example = "")
    private Integer hadAnalyzeSum;
    @ApiModelProperty(value = "已发现问题数", position = 5, example = "")
    private Integer hadFoundProblemSum;
    @ApiModelProperty(value = "日期", position = 6, example = "")
    private String sortDate;

}
