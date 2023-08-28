package com.imapcloud.nest.v2.service.dto.nest;

import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 基站信息
 *
 * @author boluo
 * @date 2023-02-15
 */
@Data
public class NestBaseDTO implements Serializable {

    /**
     * 基站uuid
     */
    private String uuid;
    /**
     * 基站概括状态
     */
    private Integer state = -1;
    /**
     * 基站基础状态
     */
    private String baseState = NestStateEnum.OFF_LINE.getChinese();

    /**
     * 基站维保状态
     */
    private Integer maintenanceState = 0;
    /**
     * 基站是否调试
     */
    private Integer nestDebug = 0;
    /**
     * 基站是否连接
     */
    private Integer nestConnected = 0;
}
