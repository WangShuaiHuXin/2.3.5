package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.PilotFileUploadInDTO;
import com.imapcloud.nest.v2.service.dto.out.PilotStsCredentialsOutDTO;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIPilotCommonService.java
 * @Description DJIPilotCommonService
 * @createTime 2022年10月19日 15:52:00
 */
public interface DJIPilotMediaService {

    /**
     * 获取授权信息
     * @return
     */
    PilotStsCredentialsOutDTO getSts();


    String uploadCallback(PilotFileUploadInDTO pilotFileUploadInDTO);



}
