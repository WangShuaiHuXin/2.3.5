package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataAnalysisCenterDetailRespVO implements Serializable {
    @ApiModelProperty(value = "数据分析中心基础表主键", position = 1, example = "")
    private String centerBaseId;
    @ApiModelProperty(value = "数据分析中心明细表主键", position = 1, example = "")
    private String centerDetailId;
    @ApiModelProperty(value = "照片主键", position = 1, example = "")
    private String photoId;
    @ApiModelProperty(value = "照片名", position = 1, example = "")
    private String photoName;
    @ApiModelProperty(value = "照片状态", position = 1, example = "")
    private Integer photoState;
    @ApiModelProperty(value = "照片推送状态", position = 1, example = "")
    private Integer pushState;
    @ApiModelProperty(value = "前端对应照片状态", position = 1, example = "")
    private Integer picStatus;
    @ApiModelProperty(value = "标签ID", position = 1, example = "")
    private String tagId;
    @ApiModelProperty(value = "任务ID", position = 1, example = "")
    private String taskId;
    @ApiModelProperty(value = "架次ID", position = 1, example = "")
    private String missionId;
    @ApiModelProperty(value = "架次记录ID", position = 1, example = "")
    private String missionRecordId;
    @ApiModelProperty(value = "基站ID", position = 1, example = "")
    private String nestId;
    @ApiModelProperty(value = "单位ID", position = 1, example = "")
    private String orgId;
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

    private String missionFlyIndex;

    private String imagePath;

    /**
     * @since 2.1.4
     */
    @ApiModelProperty(value = "AI识别状态", position = 1, example = "")
    private Integer aiAnalysis;

}
