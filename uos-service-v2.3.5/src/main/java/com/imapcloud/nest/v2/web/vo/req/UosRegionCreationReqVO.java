package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Classname RegionDetailReqVO
 * @Description 区域新建详细信息
 * @Date 2022/8/11 10:15
 * @Author Carnival
 */

@ApiModel("区域新建信息")
@Data
public class UosRegionCreationReqVO implements Serializable {

    @ApiModelProperty(value = "区域名称", position = 1,  required = true, example = "北京")
    @NotBlank(message = "{geoai_uos_cannot_empty_region_name}")
    @Length(min = 2, max = 16, message = "{geoai_uos_limited_2_16_region_name}")
    private String regionName;

    @ApiModelProperty(value = "区域描述", position = 2,  example = "首都")
    @Length(min = 0, max = 200, message = "{geoai_uos_cannot_exceed_200_char_region_description}")
    private String description;
}
