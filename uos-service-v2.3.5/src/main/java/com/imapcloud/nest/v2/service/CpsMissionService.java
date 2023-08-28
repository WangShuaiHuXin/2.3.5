package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.CpsMissionInDTO;
import com.imapcloud.nest.v2.service.dto.out.CpsMissionOutDTO;

public interface CpsMissionService {
    /**
     * 设置备降点
     *
     * @param cpsMissionLandingInDTO
     * @return
     */
    Boolean setAlternateLanding(CpsMissionInDTO.CpsMissionLandingInDTO cpsMissionLandingInDTO);

    /**
     * 是否自动前往备降点  开/关
     *
     * @param inDTO
     * @return
     */
    Boolean setAlternateLandingStatus(CpsMissionInDTO.CpsMissionLandingStatusInDTO inDTO);

    /**
     * 前往备降点
     *
     * @param inDTO
     * @return
     */
    Boolean setAlternateLandingForward(CpsMissionInDTO.CpsMissionLandingInDTO inDTO);

    /**
     * 获取备降点信息
     *
     * @param nestId
     * @return
     */
    CpsMissionOutDTO.CpsMissionAlternateInfoOutDTO getAlternateLandingInfo(String nestId);

    /**
     * 获取设置的前往备降点高度
     * @param nestId
     * @return
     */
    Double getAlternateAltitudeInfo(String nestId);
}
