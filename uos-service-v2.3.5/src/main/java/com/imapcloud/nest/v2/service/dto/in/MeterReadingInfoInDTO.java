package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 表计读数修改信息
 * @author Vastfy
 * @date 2022/12/04 16:34
 * @since 2.1.5
 */
@Data
public class MeterReadingInfoInDTO implements Serializable {

    private String screenshotPath;

    private List<MeterReadingValue> readingValues;

    @Data
    public static class MeterReadingValue implements Serializable{

        private String readingRuleId;

        private String readingValue;

    }

}
