package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Classname DataAnalysisTraceSpacetimeResqVO
 * @Description 数据分析问题统计时空追溯响应类
 * @Date 2022/10/17 10:07
 * @Author Carnival
 */
@Data
@ApiModel("统计时空追溯响应类")
@EqualsAndHashCode(callSuper = false)
public class DataAnalysisTraceSpacetimeResqVO {

    @ApiModelProperty(value = "数据分析中心基础表主键", position = 1, example = "")
    private Long centerBaseId;

    @ApiModelProperty(value = "数据分析中心明细表主键", position = 1, example = "")
    private Long centerDetailId;

    @ApiModelProperty(value = "照片主键", position = 1, example = "")
    private Long photoId;

    @ApiModelProperty(value = "照片名", position = 1, example = "")
    private String photoName;

    @ApiModelProperty(value = "照片状态", position = 1, example = "")
    private Integer photoState;

    @ApiModelProperty(value = "照片推送状态", position = 1, example = "")
    private Integer pushState;

    @ApiModelProperty(value = "标签ID", position = 1, example = "")
    private Long tagId;

    @ApiModelProperty(value = "任务ID", position = 1, example = "")
    private Long taskId;

    @ApiModelProperty(value = "架次ID", position = 1, example = "")
    private Long missionId;

    @ApiModelProperty(value = "架次记录ID", position = 1, example = "")
    private Long missionRecordsId;

    @ApiModelProperty(value = "基站ID", position = 1, example = "")
    private Long nestId;

    @ApiModelProperty(value = "标注缩略图路径", position = 1, example = "")
    private String thumImageMarkPath;

    @ApiModelProperty(value = "原图缩略图", position = 1, example = "")
    private String thumImagePath;

    @ApiModelProperty(value = "来源类型", position = 1, example = "")
    private Integer srcDataType;

    @ApiModelProperty(value = "经度", position = 1, example = "")
    private BigDecimal longitude;

    @ApiModelProperty(value = "纬度", position = 1, example = "")
    private BigDecimal latitude;

    @ApiModelProperty(value = "原图宽度", position = 1, example = "")
    private Integer originalWidth;

    @ApiModelProperty(value = "原图高度", position = 1, example = "")
    private Integer originalHeight;

    @ApiModelProperty(value = "创建时间", position = 1, example = "")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "照片创建时间", position = 1, example = "")
    private LocalDateTime photoCreateTime;

    @ApiModelProperty(value = "单位ID", position = 1, example = "")
    private String orgCode;

    @ApiModelProperty(value = "是否标记", position = 1, example = "")
    private Integer isProblem;
}
