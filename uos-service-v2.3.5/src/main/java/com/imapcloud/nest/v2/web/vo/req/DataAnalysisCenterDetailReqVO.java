package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class DataAnalysisCenterDetailReqVO implements Serializable {

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private String centerBaseId;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private String centerDetailId;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private String photoId;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private Integer photoState;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private Integer pushState;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private String tagId;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private String taskId;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private String missionId;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private String missionRecordsId;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private String nestId;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private String orgId;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private String thumImageMarkPath;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private String thumImagePath;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private Integer srcDataType;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private BigDecimal longitude;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private BigDecimal latitude;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private Integer originalWidth;

    @ApiModelProperty(value = "账号ID", position = 1, required = true, example = "10000")
    private Integer originalHeight;

}
