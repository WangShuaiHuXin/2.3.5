package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

/**
 * org dto角色信息
 * 单位的角色信息
 *
 * @author boluo
 * @date 2022-05-20
 */
@Data
public class OrgRoleInfoInDTO {

    /**
     * @deprecated 2.0.0，使用orgCode替代
     */
    @Deprecated
    private Integer unitId;

    private String orgCode;
}
