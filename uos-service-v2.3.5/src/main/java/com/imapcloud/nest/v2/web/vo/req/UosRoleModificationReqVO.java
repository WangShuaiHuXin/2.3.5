package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 角色修改信息
 * @author Vastfy
 * @date 2022/08/12 14:12
 * @since 2.0.0
 */
@ApiModel("角色修改信息")
@Data
public class UosRoleModificationReqVO implements Serializable {

    @ApiModelProperty(value = "角色名称", position = 1, required = true, example = "中科云图管理员")
    @Size(min = 1, max = 50, message = "角色名称的长度范围1-50")
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @ApiModelProperty(value = "单位编码，roleType为系统角色时，该值会被忽略", position = 2, example = "00")
    private String orgCode;

    @ApiModelProperty(value = "单位角色类型【1：单位默认角色；0：单位普通角色】，roleType为系统角色时，该值会被忽略", position = 3, example = "1")
    private Integer orgRoleType;

    @ApiModelProperty(value = "角色关联的页面资源ID列表", position = 4)
    private List<String> pageResourceIds;

}
