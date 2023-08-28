package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 基站简要信息
 *
 * @author Vastfy
 * @date 2022/8/25 16:47
 * @since 2.0.0
 */
@Data
public class NestSimpleOutDTO implements Serializable {

    /**
     * 基站ID
     */
    private String id;

    /**
     * 基站名称
     */
    private String name;

    private String nestNumber;

    private String nestUuid;

    /**
     * 类型(0-P4R固定基站；1-mini1代；2-M300；3-简易基站；4-车载基站；5-mini2代)
     */
    private Integer type;

}
