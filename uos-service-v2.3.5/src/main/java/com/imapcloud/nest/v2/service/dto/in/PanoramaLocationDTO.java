package com.imapcloud.nest.v2.service.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName Location.java
 * @Description PanoramaLocationDTO
 * @createTime 2022年09月26日 10:24:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PanoramaLocationDTO {

    /**
     * 航点id
     */
    private String airPointId;

    /**
     * 航点名
     */
    private String panoName;

    /**
     * 航点序号
     */
    private String airPointIndex;

    private BigDecimal lng;

    private BigDecimal lat;

    private BigDecimal alt;

}
