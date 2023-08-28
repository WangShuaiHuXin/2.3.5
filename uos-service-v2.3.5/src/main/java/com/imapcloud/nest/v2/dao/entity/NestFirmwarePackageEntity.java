package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基站已安装固件版本信息表实体
 * @author vastfy
 * @date 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_nest_firmware_package")
public class NestFirmwarePackageEntity extends GenericEntity {

    /**
     * 基站唯一标识
     */
    @Deprecated
    private Long nestId;

    /**
     * 基站唯一标识
     */
    private String baseNestId;

    /**
     * 安装包唯一标识
     */
    private Long packageId;

    /**
     * 安装包类型【1：CPS；2：MPS】，冗余字段
     */
    private Integer packageType;

    /**
     * 安装包名称，冗余字段
     */
    private String packageName;

    /**
     * 安装包版本，冗余字段
     */
    private String packageVersion;

    /**
     * 更新状态
     */
    private Integer state;

    /**
     * 无人机标识
     */
    private Integer uavWhich;

}
