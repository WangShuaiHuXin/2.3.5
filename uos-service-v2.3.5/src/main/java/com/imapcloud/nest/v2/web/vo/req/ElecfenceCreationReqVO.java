package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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
public class ElecfenceCreationReqVO implements Serializable {

    @ApiModelProperty(value = "电子围栏名称", position = 1, example = "新建禁飞区")
    @NotBlank(message = "电子围栏名称不能为空")
    @Length(min = 1, max = 50, message = "电子围栏长度限制1~50个字符")
    private String name;

    @ApiModelProperty(value = "电子围栏类型【1：适飞区； 2：禁飞区】", position = 2, example = "1")
    @Min(value = 1, message = "电子围栏类型只能是1（适飞区）或2（禁飞区）")
    @Max(value = 2, message = "电子围栏类型只能是1（适飞区）或2（禁飞区）")
    private Integer type;

    @ApiModelProperty(value = "电子围栏状态【1：开启；2：关闭】", position = 3, example = "1")
    @Min(value = 1, message = "电子围栏状态只能是1（开启）或2（关闭）")
    @Max(value = 2, message = "电子围栏状态只能是1（开启）或2（关闭）")
    private Integer state;

    @ApiModelProperty(value = "电子围栏坐标点", position = 4)
    @NotBlank(message = "电子围栏坐标点不能为空")
    private String coordinates;

    @ApiModelProperty(value = "电子围栏高度【单位：m】", position = 5, example = "16")
    @Min(value = 0, message = "电子围栏高度不能低于0m")
    @Max(value = 500, message = "电子围栏高度不能超过500m")
    private Integer height;

    @ApiModelProperty(value = "是否共享【默认值：false】", position = 6, example = "false")
    private Boolean shared = Boolean.FALSE;

    @ApiModelProperty(value = "单位编码", position = 7, example = "000")
    @NotBlank(message = "电子围栏所属单位编码不能为空")
    private String orgCode;

    /**
     * 有效期开始时间
     */
    @ApiModelProperty(value = "电子围栏有效期开始时间，默认为系统最新时间", position = 8, example = "2023-03-09 00:00:00")
    private LocalDateTime effectiveStartTime;

    /**
     * 有效期截止时间
     */
    @ApiModelProperty(value = "电子围栏有效期截止时间，默认为2100年", position = 9, example = "2100-01-01 00:00:00")
    private LocalDateTime effectiveEndTime;

    /**
     * 是否永久有效
     */
    @ApiModelProperty(value = "是否永久有效", position = 10, example = "false")
    private Boolean neverExpired;

}
