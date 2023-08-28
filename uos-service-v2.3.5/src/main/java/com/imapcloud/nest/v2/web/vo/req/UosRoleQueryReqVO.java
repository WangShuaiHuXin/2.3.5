package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 角色信息检索条件
 * @author Vastfy
 * @date 2022/09/06 11:52
 * @since 2.0.0
 */
@ApiModel("角色信息检索条件")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UosRoleQueryReqVO extends PageInfo {

    @ApiModelProperty(value = "应用类型【取字典`GEOAI_APPLICATION_TYPE`数据项值】", position = 11, required = true, example = "uos")
    private String appType;

    @ApiModelProperty(value = "角色类型【取字典`GEOAI_ROLE_TYPE`数据项值】", position = 12, required = true, example = "1")
    private Integer roleType;

    @ApiModelProperty(value = "角色名称（支持模糊检索）", position = 12, example = "管理员")
    private String roleName;

    @ApiModelProperty(value = "单位编码", position = 13, example = "0001")
    private String orgCode;

    @ApiModelProperty(value = "应用类型前缀", position = 14, required = true, example = "geoai-uos")
    private String appTypePrefix;

}
