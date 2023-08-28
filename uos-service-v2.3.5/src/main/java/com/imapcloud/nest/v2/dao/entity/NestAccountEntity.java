package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 单位角色表实体
 *
 * @author boluo
 * @date 2022-05-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("base_nest_account")
public class NestAccountEntity extends GenericEntity {

    /**
     * 基站自增id
     */
    @Deprecated
    private String nestId;

    /**
     * 账号id
     */
    private String accountId;

    /**
     * 用户操控基站状态 0：不可控 1：可控
     */
    private int nestControlStatus;

    private String baseNestId;
}
