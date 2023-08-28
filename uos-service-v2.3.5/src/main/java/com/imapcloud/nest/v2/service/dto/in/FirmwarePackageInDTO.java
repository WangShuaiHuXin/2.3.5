package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 固件安装包信息
 * @author Vastfy
 * @date 2022/07/13 10:35
 * @since 1.9.7
 */
@Data
public class FirmwarePackageInDTO implements Serializable {

    /**
     * 更新包类型【1：CPS固件；2：MPS固件】
     */
    private Integer type;

    /**
     * 安装包名称
     */
    private String fileName;

    /**
     * 安装包存储路径
     */
    private String filePath;

    /**
     * 安装包存储路径
     */
    private String version;

    /**
     * 安装包大小（单位：字节）
     */
    private Long fileSize;

    /**
     * 安装包版本描述（更新日志）
     */
    private String versionDesc;

}
