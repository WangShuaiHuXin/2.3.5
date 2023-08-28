package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 基站固件安装包记录
 * @author Vastfy
 * @date 2022/07/13 10:35
 * @since 1.9.7
 */
@Data
public class NestFirmwarePackageInDTO implements Serializable {

    /**
     * 安装包ID
     */
    private String packageId;

    /**
     * 基站ID
     */
    private String nestId;

    /**
     * 更新包类型【1：CPS固件；2：MPS固件】
     */
    private Integer type;

    /**
     * 安装包名称
     */
    private String apkFileName;

    /**
     * 安装包存储路径
     */
    private String apkFilePath;

    /**
     * 安装包版本
     */
    private String version;

    /**
     * 无人机标识
     */
    private Integer uavWhich;

}
