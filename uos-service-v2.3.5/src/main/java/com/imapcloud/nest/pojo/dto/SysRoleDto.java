package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 权限信息表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
public class SysRoleDto {

    /**
     * 单位id
     */
    private String unitId;

    private String id;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String name;
}
