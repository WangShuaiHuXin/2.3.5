package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 应用页面资源树信息
 * @author Vastfy
 * @date 2022/08/29 11:12
 * @since 2.0.0
 */
@ApiModel("应用页面资源树信息")
@Data
public class UosPageResourceNodeRespVO implements Serializable {

    @ApiModelProperty(value = "应用类型【区分UOS和UDA】", position = 1, required = true, example = "geoai-uos-background")
    private String appType;

    @ApiModelProperty(value = "资源ID", position = 2, required = true, example = "NULL")
    private String pageResourceId;

    @ApiModelProperty(value = "资源类型【取字典`GEOAI_PAGE_RESOURCE_TYPE`数据项值】", position = 3, required = true, example = "-1")
    private Integer resourceType;

    @ApiModelProperty(value = "资源唯一标识", position = 4, required = true, example = "ROLE_ADD")
    private String pageResourceKey;

    @ApiModelProperty(value = "资源名称", position = 5, required = true, example = "角色新增")
    private String pageResourceName;

    @ApiModelProperty(value = "页面资源信息", position = 6, required = true, example = "[]")
    private List<UosPageResourceNodeRespVO> children;

}
