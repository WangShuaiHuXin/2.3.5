package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 角色新建信息
 * @author Vastfy
 * @date 2022/08/12 14:12
 * @since 2.0.0
 */
@ApiModel("角色新建信息")
@Data
@EqualsAndHashCode(callSuper = true)
public class UosRoleCreationReqVO extends UosRoleModificationReqVO {

    @ApiModelProperty(value = "应用类型【取字典`GEOAI_APPLICATION_TYPE`数据项值】", position = 10, required = true, example = "UOS_FOREGROUND_SYSTEM")
    @NotNull(message = "{geoai_uos_cannot_empty_application_type}")
    private String appType;

    @ApiModelProperty(value = "角色类型【0：系统角色；1：单位角色】", position = 10, required = true, example = "1")
    @NotNull(message = "角色类型不能为空")
    private Integer roleType;

}
