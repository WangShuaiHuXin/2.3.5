package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色信息
 * @author Vastfy
 * @date 2022/09/06 11:50
 * @since 2.0.0
 */
@ApiModel("角色基本信息")
@Data
public class UosRoleBasicRespVO implements Serializable {

    @ApiModelProperty(value = "角色ID", position = 1)
    private String roleId;

    @ApiModelProperty(value = "角色名称", position = 2)
    private String roleName;

    @ApiModelProperty(value = "角色类型", position = 3)
    private Integer roleType;

    @ApiModelProperty(value = "应用类型", position = 4)
    private String appType;

    @ApiModelProperty(value = "单位角色类型", position = 5)
    private Integer orgRoleType;

    @ApiModelProperty(value = "单位编码", position = 6)
    private String orgCode;

    @ApiModelProperty(value = "单位名称", position = 7)
    private String orgName;

    @ApiModelProperty(value = "创建时间", position = 8)
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新时间", position = 9)
    private LocalDateTime modifiedTime;

}
