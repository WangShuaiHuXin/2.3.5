package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.util.List;

/**
 * 角色页面资源修改
 *
 * @author boluo
 * @date 2022-05-24
 */
@Data
public class ResourceRolePageEditInDTO {

    private String roleId;

    private List<String> pageResourceIdList;
}
