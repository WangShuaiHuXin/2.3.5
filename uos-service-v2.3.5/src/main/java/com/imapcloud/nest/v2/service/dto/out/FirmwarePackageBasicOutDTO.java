package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 固件安装包基本信息
 * @author Vastfy
 * @date 2022/07/08 11:12
 * @since 1.9.7
 */
@Data
public class FirmwarePackageBasicOutDTO implements Serializable {

    /**
     * 安装包ID
     */
    private String id;

    /**
     * 安装包名称
     */
    private String name;

    /**
     * 安装包类型【1：CPS；2：MPS】
     */
    private Integer type;

    /**
     * 安装包版本号
     */
    private String version;

    /**
     * 无人机标识
     */
    private Integer uavWhich;

}
