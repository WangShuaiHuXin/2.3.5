package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 基站-用户关联信息
 */

@Data
public class NestAccountOutDTO implements Serializable {

    private String accountId;

    private String nestId;
    /**
     * 用户操控基站状态 0：不可控 1：可控
     */
    private int nestControlStatus;
}
