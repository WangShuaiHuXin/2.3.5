package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 民航局云端无人机信息
 * @author Vastfy
 * @date 2023/03/08 15:12
 * @since 2.2.5
 */
@Data
public class CaacCloudUavOutDTO implements Serializable {

    private String cpn;

    private String cpnName;

    private BigDecimal lng;

    private BigDecimal lat;

    private BigDecimal height;

    private BigDecimal speed;

    private Integer angle;

    private String uavId;

}
