package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 固件安装包信息表实体
 * @author vastfy
 * @date 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_firmware_package")
public class FirmwarePackageEntity extends GenericEntity {

    /**
     * 安装包唯一标识
     */
    private Long packageId;

    /**
     * 安装包名称
     */
    private String name;

    /**
     * 安装包类型【1：CPS；2：MPS】
     */
    private Integer type;

    /**
     * 安装包大小（单位：字节）
     */
    private Long size;

    /**
     * 安装包版本
     */
    private String version;

    /**
     * 安装包版本更新日志
     */
    private String versionDesc;

    /**
     * 安装包存储路径
     */
    private String storePath;

}
