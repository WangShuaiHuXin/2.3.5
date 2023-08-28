package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class DataAnalysisDetailMarkRespVO implements Serializable {
    @ApiModelProperty(value = "基础主键", position = 1, example = "")
    private String centerBaseId;
    @ApiModelProperty(value = "明细主键", position = 1, example = "")
    private String centerDetailId;
    @ApiModelProperty(value = "照片ID", position = 1, example = "")
    private String photoId;
    @ApiModelProperty(value ="照片名", position = 1, example = "")
    private String photoName;
    @ApiModelProperty(value = "前端对应照片状态", position = 1, example = "")
    private Integer picStatus;
    @ApiModelProperty(value = "单位ID", position = 1, example = "")
    private String orgId;
    @ApiModelProperty(value = "照片路径", position = 1, example = "")
    private String imagePath;
    @ApiModelProperty(value = "经度", position = 1, example = "")
    private BigDecimal longitude;
    @ApiModelProperty(value = "纬度", position = 1, example = "")
    private BigDecimal latitude;

    private String orgCode;

    private LocalDateTime photoCreateTime;

    private String thumImagePath;

    @ApiModelProperty(value = "照片海拔高度", position = 1, example = "")
    private Double altitude;

    @ApiModelProperty(value = "镜头类型", position = 1, example = "")
    private Integer lenType;
}
