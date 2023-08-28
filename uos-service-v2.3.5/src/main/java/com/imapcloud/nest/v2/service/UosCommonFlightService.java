package com.imapcloud.nest.v2.service;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosCommonFlightService.java
 * @Description UosCommonFlightService
 * @createTime 2022年08月18日 15:29:00
 */
public interface UosCommonFlightService {



    /**
     * 空中回巢
     * @param nestId
     * @return
     */
    boolean flightBack(String nestId);
}
