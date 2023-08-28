package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 区域基站信息响应视图对象示例
 * @author Vastfy
 * @date 2022/07/08 11:12
 * @since 1.9.7
 */
@ApiModel("区域基站信息")
@Data
public class RegionNestRespVO implements Serializable {

    @ApiModelProperty(value = "区域ID", position = 1, required = true, example = "9527")
    private String regionId;

    @ApiModelProperty(value = "区域名称", position = 2, required = true, example = "广州")
    private String regionName;

    @ApiModelProperty(value = "基站信息列表", position = 3, required = true)
    private List<NestBasicRespVO> nestInfos;

}
