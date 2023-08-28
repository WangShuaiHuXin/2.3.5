package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIDockAircraftInfoDTO.java
 * @Description DJIDockAircraftInfoDTO
 * @createTime 2022年09月16日 11:23:00
 */
@Data
public class DJIDockAircraftInfoOutDTO {
    private DJIDockInfoOutDTO djiDockInfoDTO;
    private DJIAircraftInfoOutDTO djiAircraftInfoDTO;
}
