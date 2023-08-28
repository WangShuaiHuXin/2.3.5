package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色新建信息
 * @author Vastfy
 * @date 2022/08/18 11:12
 * @since 2.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UosRoleCreationInDTO extends UosRoleModificationInDTO {

    /**
     * 应用类型
     */
    private String appType;

    /**
     * 角色类型【0：系统角色；1：单位角色】
     */
    private Integer roleType;

}
