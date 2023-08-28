package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.out.AIAutoDiscernSettingsOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DiscernFunctionSettingOutDTO;
import com.imapcloud.nest.v2.web.vo.req.AIDiscernFunctionInfoReqVO;

import java.util.List;

public interface PowerDataService {

    List<DiscernFunctionSettingOutDTO> getAllDiscernFunctionInfos(String orgCode);

    void modifyOrgAIAutoDiscernSettings(String orgCode);

    AIAutoDiscernSettingsOutDTO getOrgAIAutoDiscernSettings(String orgCode);

    List<DiscernFunctionSettingOutDTO.DiscernFunctionInfoOutDTO> getDiscernFunctionInfos(String orgCode, Integer discernType);

    void saveDiscernFunctionInfos(String orgCode, AIDiscernFunctionInfoReqVO body);
}
