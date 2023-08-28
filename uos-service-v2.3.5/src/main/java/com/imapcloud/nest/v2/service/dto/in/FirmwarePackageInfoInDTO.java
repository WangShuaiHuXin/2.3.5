package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;
import org.springframework.core.io.Resource;

import java.io.Serializable;

/**
 * 固件安装包信息
 * @author Vastfy
 * @date 2022/07/13 10:35
 * @since 1.9.7
 */
@Data
public class FirmwarePackageInfoInDTO implements Serializable {

    /**
     * 安装包类型（zip或者apk）
     */
    private String packageType;

    /**
     * 固件安装包版本
     */
    private String packageVersion;

    /**
     * 固件安装包大小（单位：字节）
     */
    private Long packageSize;

    /**
     * 安装包数据流
     */
    private Resource packageResource;

}
