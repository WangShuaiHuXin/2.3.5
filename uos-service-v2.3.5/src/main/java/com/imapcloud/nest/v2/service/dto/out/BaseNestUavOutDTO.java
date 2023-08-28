package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseNestUavOutDTO implements Serializable {
    /**
     * 无人机id
     */
    private String uavId;

    /**
     * 基站id
     */
    private String nestId;
}
