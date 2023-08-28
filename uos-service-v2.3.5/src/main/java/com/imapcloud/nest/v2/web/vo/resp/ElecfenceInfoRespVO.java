package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 电子围栏信息响应视图
 * @author Vastfy
 * @date 2022/9/23 17:27
 * @since 2.1.0
 */
@Data
@ApiModel("电子围栏信息")
public class ElecfenceInfoRespVO implements Serializable {

    @ApiModelProperty(value = "电子围栏ID", position = 1, example = "9527")
    private String id;;

    @ApiModelProperty(value = "电子围栏名称", position = 1, example = "新建禁飞区")
    private String name;

    @ApiModelProperty(value = "电子围栏类型【1：适飞区； 2：禁飞区】", position = 2, example = "1")
    private Integer type;

    @ApiModelProperty(value = "电子围栏状态【1：开启；2：关闭】", position = 3, example = "1")
    private Integer state;

    @ApiModelProperty(value = "电子围栏坐标点", position = 4)
    private String coordinates;

    @ApiModelProperty(value = "电子围栏高度【单位：m】", position = 5, example = "16")
    private Integer height;

    @ApiModelProperty(value = "是否共享", position = 6, example = "false")
    private Boolean shared;

    @ApiModelProperty(value = "电子围栏所属单位编码", position = 7, example = "000001")
    private String orgCode;

    /**
     * 有效期开始时间
     */
    @ApiModelProperty(value = "电子围栏有效期开始时间", position = 8, example = "2023-03-09 00:00:00")
    private LocalDateTime effectiveStartTime;

    /**
     * 有效期截止时间
     */
    @ApiModelProperty(value = "电子围栏有效期截止时间", position = 9, example = "2100-01-01 00:00:00")
    private LocalDateTime effectiveEndTime;

    /**
     * 是否永久有效
     */
    @ApiModelProperty(value = "是否永久有效", position = 10, example = "false")
    private Boolean neverExpired;

}
