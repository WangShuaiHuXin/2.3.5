package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname RegionBasicRespVO
 * @Description 区域简要信息
 * @Date 2022/8/26 10:15
 * @Author Carnival
 */
@ApiModel("区域简要信息")
@Data
public class UosRegionSimpleRespVO implements Serializable {

    @ApiModelProperty(value = "区域编码", position = 1,  example = "45653333134685566")
    private String regionId;

    @ApiModelProperty(value = "区域名称", position = 2,  example = "北京")
    private String regionName;
}
