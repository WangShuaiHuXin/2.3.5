package com.imapcloud.nest.v2.manager.dataobj.out;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色信息
 *
 * @author boluo
 * @date 2022-05-18
 */
@ApiModel("角色信息")
@Data
public class AccountRoleInfoOutDO implements Serializable {

    @ApiModelProperty(value = "账号ID", position = 1)
    private String accountId;

    @ApiModelProperty(value = "角色信息列表", position = 2)
    private List<RoleInfoOutDO> roleInfos;

}
