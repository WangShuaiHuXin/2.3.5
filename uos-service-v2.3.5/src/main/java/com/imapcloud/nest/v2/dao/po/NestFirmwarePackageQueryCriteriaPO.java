package com.imapcloud.nest.v2.dao.po;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * 基站固件更新记录分页查询条件
 * @author Vastfy
 * @date 2022/5/25 17:46
 * @since 2.0.0
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class NestFirmwarePackageQueryCriteriaPO extends QueryCriteriaDo<NestFirmwarePackageQueryCriteriaPO> {

    /**
     * 基站ID
     */
    private String baseNestId;

    /**
     * 更新包类型【1：CPS固件；2：MPS固件】
     */
    private Integer type;

    /**
     * 更新包名称（支持模糊检索）
     */
    private String name;

    /**
     * 无人机标识
     */
    private Integer uavWhich;

}
