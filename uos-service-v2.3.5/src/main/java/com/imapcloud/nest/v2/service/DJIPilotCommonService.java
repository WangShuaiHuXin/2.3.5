package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.out.DJIPilotCommonResultOutDTO;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIPilotCommonService.java
 * @Description DJIPilotCommonService
 * @createTime 2022年10月19日 15:52:00
 */
public interface DJIPilotCommonService {

    /**
     * 获取授权信息
     * @return
     */
    DJIPilotCommonResultOutDTO.PilotLicenseResultOutDTO getLicense();

    /**
     * 根据uuid获取Mqtt信息
     * @param nestUUid
     * @return
     */
    DJIPilotCommonResultOutDTO.PilotMqttResultOutDTO getMqttInfo(String nestUUid);

    /**
     * 绑定无人机跟基站
     * @param nestSn
     * @param uavSn
     * @return
     */
    boolean bindNestAndUav(String nestSn, String uavSn);



}
