package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class DataAnalysisCenterBaseRespVO implements Serializable {
    @ApiModelProperty(value = "主键", position = 1, example = "")
    private String centerBaseId;
    @ApiModelProperty(value = "基础任务名", position = 2, example = "")
    private String baseName;
    @ApiModelProperty(value = "标签ID", position = 3, example = "")
    private String tagId;
    @ApiModelProperty(value = "标签名", position = 4, example = "")
    private String tagName;
    @ApiModelProperty(value = "任务ID", position = 5, example = "")
    private String taskId;
    @ApiModelProperty(value = "任务名", position = 6, example = "")
    private String taskName;
    @ApiModelProperty(value = "架次ID", position = 7, example = "")
    private String missionId;
    @ApiModelProperty(value = "架次记录ID", position = 8, example = "")
    private String missionRecordsId;
    @ApiModelProperty(value = "基站ID", position = 9, example = "")
    private String nestId;
    @ApiModelProperty(value = "基站名", position = 10, example = "")
    private String nestName;
    @ApiModelProperty(value = "单位ID", position = 11, example = "")
    private String orgId;
    @ApiModelProperty(value = "待分析数", position = 12, example = "")
    private Integer needAnalyzeSum;
    @ApiModelProperty(value = "待确认有问题数", position = 13, example = "")
    private Integer needConfirmProblemSum;
    @ApiModelProperty(value = "待确认无问题数", position = 14, example = "")
    private Integer needConfirmNoProblemSum;
    @ApiModelProperty(value = "有问题数", position = 15, example = "")
    private Integer problemSum;
    @ApiModelProperty(value = "无问题数", position = 16, example = "")
    private Integer noProblemSum;
    @ApiModelProperty(value = "创建时间", position = 17, example = "")
    private LocalDateTime createdTime;
    @ApiModelProperty(value = "航线类型", position = 18, example = "")
    private Integer taskType;
    @ApiModelProperty(value = "航线序号ID", position = 19, example = "")
    private Integer missionSeqId;
    @ApiModelProperty(value = "任务架次记录时间", position = 20, example = "")
    private LocalDateTime missionRecordTime;
    @ApiModelProperty(value = "变电站类型", position = 21, example = "")
    private Integer subType;
    @ApiModelProperty(value = "更新时间", position = 22, example = "")
    private LocalDateTime modifiedTime;
    @ApiModelProperty(value = "架次号", position = 23, example = "")
    private String missionFlyIndex;
    @ApiModelProperty(value = "机巢类型", position = 24, example = "")
    private Integer NestType;
}
