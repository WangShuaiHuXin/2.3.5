package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @Classname RegionReqVO
 * @Description 区域修改信息
 * @Date 2022/8/11 10:11
 * @Author Carnival
 */
@ApiModel("区域修改信息")
@Data
public class UosRegionModificationReqVO implements Serializable {

    @ApiModelProperty(value = "区域ID", position = 1, required = true, example = "1000200128200")
    @NotNull(message = "区域ID不能为空")
    private String regionId;

    @ApiModelProperty(value = "区域名称", position = 2, required = true, example = "北京")
    @Length(min = 1, max = 80, message = "{geoai_uos_cannot_exceed_80_char_region_description}")
    @NotNull(message = "{geoai_uos_cannot_empty_region_name}")
    private String regionName;

    @ApiModelProperty(value = "区域描述", position = 3,  example = "广东省份")
    @Length(min = 1, max = 200, message = "{geoai_uos_cannot_exceed_200_char_region_description}")
    private String description;
}
