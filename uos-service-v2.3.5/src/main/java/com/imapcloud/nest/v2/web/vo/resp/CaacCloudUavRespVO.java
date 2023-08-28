package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 民航局云端无人机信息
 * @author Vastfy
 * @date 2023/03/08 15:12
 * @since 2.2.5
 */
@ApiModel("民航局云端无人机信息")
@Data
public class CaacCloudUavRespVO implements Serializable {

    @ApiModelProperty(value = "云运营商编号", position = 1, required = true, example = "001")
    private String cpn;

    @ApiModelProperty(value = "所属厂商编号", position = 2, required = true, example = "U-Cloud")
    private String cpnName;

    @ApiModelProperty(value = "无人机位置经度", position = 3, required = true, example = "117.3485816")
    private BigDecimal lng;

    @ApiModelProperty(value = "无人机位置维度", position = 4, required = true, example = "36.7119600")
    private BigDecimal lat;

    @ApiModelProperty(value = "无人机位置高度", position = 5, required = true, example = "55.72")
    private BigDecimal height;

    @ApiModelProperty(value = "无人机速度", position = 6, required = true, example = "8.47")
    private BigDecimal speed;

    @ApiModelProperty(value = "无人机角度", position = 7, required = true, example = "143")
    private Integer angle;

    @ApiModelProperty(value = "无人机编号", position = 8, required = true, example = "001ⅠAD2374")
    private String uavId;

}
