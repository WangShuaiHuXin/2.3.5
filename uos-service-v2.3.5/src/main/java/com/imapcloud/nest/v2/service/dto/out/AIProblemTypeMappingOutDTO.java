package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * AI问题类型映射
 * @author Vastfy
 * @date 2022/11/8 14:46
 * @since 2.1.4
 */
@Data
public class AIProblemTypeMappingOutDTO implements Serializable {

    /**
     * AI问题类型ID
     */
    private String aiProblemTypeId;

    /**
     * uos问题类型ID
     */
    private Long problemTypeId;

    /**
     * UOS问题类型名称
     */
    private String problemTypeName;

    private Integer industryType;

}
