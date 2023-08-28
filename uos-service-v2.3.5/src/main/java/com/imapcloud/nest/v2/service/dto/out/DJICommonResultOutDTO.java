package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJICommonResultOutDTO.java
 * @Description DJICommonResultOutDTO
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DJICommonResultOutDTO implements Serializable {
    @Data
    public static class CommonResultOutDTO {
        private Integer result;

        private String statusCode;

        private String statusStr;
    }

    @Data
    public static class LiveResultOutDTO {

        private Integer result;

        private String info;


    }
}
