package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

/**
 * 删除角色
 *
 * @author boluo
 * @date 2022-05-22
 */
@Data
public class RoleDeleteInDTO {

    private Long roleId;

    private String accountId;
}
