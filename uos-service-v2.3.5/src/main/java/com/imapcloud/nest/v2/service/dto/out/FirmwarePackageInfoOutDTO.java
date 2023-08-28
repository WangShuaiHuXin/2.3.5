package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 固件安装包信息
 * @author Vastfy
 * @date 2022/07/13 10:42
 * @since 1.9.7
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FirmwarePackageInfoOutDTO extends FirmwarePackageBasicOutDTO {

    /**
     * 安装包版本描述（更新日志）
     */
    private String versionDesc;

    /**
     * 无人机标识
     */
    private Integer uavWhich;


}
