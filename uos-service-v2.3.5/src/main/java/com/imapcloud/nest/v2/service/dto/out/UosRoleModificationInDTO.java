package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色修改信息
 * @author Vastfy
 * @date 2022/08/18 11:12
 * @since 2.0.0
 */
@Data
public class UosRoleModificationInDTO implements Serializable {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 单位角色类型【0：单位默认角色；1：单位普通角色】
     */
    private Integer orgRoleType;

    /**
     * 角色关联的页面资源ID列表
     */
    private List<String> pageResourceIds;

}
