package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIAerographyInfoOutDTO.java
 * @Description DJIAerographyInfoOutDTO
 * @createTime 2022年09月16日 11:23:00
 */
@Data
@Accessors(chain = true)
public class DJIAerographyInfoOutDTO {

    /**
     * 降雨量
     */
    private BigDecimal rainfall;

    /**
     * 风速
     */
    private BigDecimal windSpeed;

    /**
     * 环境温度
     */
    private BigDecimal environmentTemperature;

    /**
     * 环境湿度
     */
    private BigDecimal environmentHumidity;

}
