package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;


@Data
public class LimitDistanceInDTO {
    /**
     * 基站id
     */
    private String nestId;

    /**
     * 需要限制的高度
     */
    private Integer distance;
}
