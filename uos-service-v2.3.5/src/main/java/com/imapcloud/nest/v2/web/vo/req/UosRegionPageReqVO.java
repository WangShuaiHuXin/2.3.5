package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;


/**
 * @Classname RegionReqVO
 * @Description 区域查询信息
 * @Date 2022/8/11 10:11
 * @Author Carnival
 */
@ApiModel("区域查询信息")
@Data
public class UosRegionPageReqVO extends PageInfo implements Serializable {

    @ApiModelProperty(value = "区域名称（同时支持区域名称模糊检索）",  example = "北京")
    @Length(min = 1, max = 80, message = "{geoai_uos_cannot_exceed_80_char_region_description}")
    private String regionName;
}
