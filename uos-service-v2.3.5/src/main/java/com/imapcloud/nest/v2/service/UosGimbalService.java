package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.GimbalInDTO;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosGimbalService.java
 * @Description UosGimbalService
 * @createTime 2022年08月19日 18:11:00
 */
public interface UosGimbalService {

    /**
     * 云台控制
     * @param gimbalInDTO
     * @return
     */
    Boolean gimbal(GimbalInDTO gimbalInDTO);

}
