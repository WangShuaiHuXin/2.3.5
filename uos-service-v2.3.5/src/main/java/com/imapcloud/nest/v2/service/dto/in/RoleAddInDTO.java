package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

/**
 * 角色添加dto
 *
 * @author boluo
 * @date 2022-05-20
 */
@Data
public class RoleAddInDTO {

    private String orgCode;

    private String roleName;

    private String accountId;
}
