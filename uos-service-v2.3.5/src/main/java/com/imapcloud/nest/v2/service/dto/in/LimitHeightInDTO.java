package com.imapcloud.nest.v2.service.dto.in;


import lombok.Data;

/**
 * @author wmin
 */
@Data
public class LimitHeightInDTO {
    /**
     * 基站id
     */
    private String nestId;

    /**
     * 需要限制的高度
     */
    private Integer height;
}
