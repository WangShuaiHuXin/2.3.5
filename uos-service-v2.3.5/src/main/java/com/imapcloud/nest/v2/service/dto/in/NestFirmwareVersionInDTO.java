package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 固件版本查询条件
 * @author Vastfy
 * @date 2022/07/13 10:35
 * @since 1.9.7
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NestFirmwareVersionInDTO extends FirmwareVersionInDTO {

    /**
     * 基站ID
     */
    private String nestId;

    /**
     * 无人机标识
     */
    private Integer uavWhich;

}
