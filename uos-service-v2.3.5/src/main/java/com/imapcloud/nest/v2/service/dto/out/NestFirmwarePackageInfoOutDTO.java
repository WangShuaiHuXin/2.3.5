package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 基站固件安装包更新信息
 * @author Vastfy
 * @date 2022/07/13 10:42
 * @since 1.9.7
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NestFirmwarePackageInfoOutDTO extends FirmwarePackageBasicOutDTO {

    /**
     * 基站ID
     */
    private String nestId;

    /**
     * 更新时间
     */
    private LocalDateTime modifiedTime;

    /**
     * 更新状态【-1：安装超时；0：安装中；1：安装成功；2：安装失败】
     */
    private Integer state;

    /**
     * 无人机标识
     */
    private Integer uavWhich;

}
