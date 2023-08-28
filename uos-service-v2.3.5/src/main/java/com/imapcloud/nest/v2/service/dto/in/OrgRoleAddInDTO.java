package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

/**
 * 创建角色以及保存单位角色关系
 *
 * @author boluo
 * @date 2022-05-20
 */
@Data
public class OrgRoleAddInDTO {

    private int unitId;

    private String accountId;
}
