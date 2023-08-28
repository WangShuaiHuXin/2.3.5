package com.imapcloud.nest.v2.common.enums;

import com.geoai.common.core.enums.ITypeEnum;

/**
 * 单位角色类型
 *
 * @author Vastfy
 * @date 2022/5/24 17:34
 * @since 1.0.0
 */
public enum OrgRoleTypeEnum implements ITypeEnum<OrgRoleTypeEnum> {

    /**
     * 0：普通管理员
     */
    NORMAL,

    /**
     * 1: 默认管理员
     */
    DEFAULT
    ;

    @Override
    public int getType() {
        return ordinal();
    }
}
