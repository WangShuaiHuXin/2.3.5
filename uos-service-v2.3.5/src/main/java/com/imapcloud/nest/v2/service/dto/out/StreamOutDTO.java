package com.imapcloud.nest.v2.service.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName StreamOutDTO.java
 * @Description StreamOutDTO
 * @createTime 2022年08月18日 16:17:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamOutDTO {
    private String nestId;
    private Integer mode;
    private String modeStr;
}
