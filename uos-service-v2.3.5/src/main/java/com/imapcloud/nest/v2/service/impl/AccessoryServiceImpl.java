package com.imapcloud.nest.v2.service.impl;

import com.imapcloud.nest.v2.dao.po.in.AccessoryInPO;
import com.imapcloud.nest.v2.dao.po.out.AccessoryOutPO;
import com.imapcloud.nest.v2.manager.cps.CpsAccessoryManager;
import com.imapcloud.nest.v2.service.AccessoryService;
import com.imapcloud.nest.v2.service.dto.in.AccessoryInDTO;
import com.imapcloud.nest.v2.service.dto.out.AccessoryAuthStatusOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class AccessoryServiceImpl implements AccessoryService {

    @Resource
    private CpsAccessoryManager cpsAccessoryManager;

    @Override
    public AccessoryAuthStatusOutDTO accessoryAuthStatus(String nestId) {
        AccessoryOutPO.AccessoryAuthOutPO outPO = cpsAccessoryManager.accessoryAuthStatus(nestId);
        AccessoryAuthStatusOutDTO outDTO = new AccessoryAuthStatusOutDTO();
        BeanUtils.copyProperties(outPO, outDTO);
        return outDTO;
    }

    @Override
    public Boolean accessorySendCaptcha(AccessoryInDTO.AccessoryCaptchaInDTO inDTO) {
        AccessoryInPO.AccessoryCaptchaInPO inPO = new AccessoryInPO.AccessoryCaptchaInPO();
        BeanUtils.copyProperties(inDTO, inPO);
        return cpsAccessoryManager.accessorySendCaptcha(inPO);
    }

    @Override
    public Boolean sendCertification(AccessoryInDTO.AccessoryCaptchaInDTO inDTO) {
        AccessoryInPO.AccessoryCaptchaInPO inPO = new AccessoryInPO.AccessoryCaptchaInPO();
        BeanUtils.copyProperties(inDTO, inPO);
        return cpsAccessoryManager.sendCertification(inPO);
    }

    @Override
    public Boolean setTransmission(String nestId, Boolean enable) {
        return cpsAccessoryManager.setTransmission(nestId, enable);
    }

    @Override
    public Boolean getTransmission(String nestId) {
        return cpsAccessoryManager.getTransmission(nestId);
    }
}
