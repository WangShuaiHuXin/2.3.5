package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class SysRoleFunctionDto {
    /**
     * 菜单id
     */
    private List<Long> menuIds;

    private String roleId;
}
