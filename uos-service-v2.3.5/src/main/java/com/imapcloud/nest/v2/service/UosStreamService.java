package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.out.StreamOutDTO;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosStreamService.java
 * @Description UosStreamService
 * @createTime 2022年08月18日 16:12:00
 */
public interface UosStreamService {

    /**
     * 设置推流模式
     * @param nestId
     * @param mode
     * @return
     */
    boolean setStream(String nestId, Integer mode , Integer uavWhich);

    /**
     *  获取推流模式
     * @param nestId
     * @return
     */
    StreamOutDTO getStream(String nestId , Integer uavWhich);

}
