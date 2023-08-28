package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.AccessoryInDTO;
import com.imapcloud.nest.v2.service.dto.out.AccessoryAuthStatusOutDTO;

public interface AccessoryService {
    /**
     * 获取lte认证状态信息
     * @param nestId
     * @return
     */
    AccessoryAuthStatusOutDTO accessoryAuthStatus(String nestId);

    /**
     * 发送验证码
     * @return
     */
    Boolean accessorySendCaptcha(AccessoryInDTO.AccessoryCaptchaInDTO inDTO);

    /**
     * 发送实名认证请求
     * @param inDTO
     * @return
     */
    Boolean sendCertification(AccessoryInDTO.AccessoryCaptchaInDTO inDTO);

    /**
     * 指令-设置增强图传状态
     * @param nestId
     * @param enable
     * @return
     */
    Boolean setTransmission(String nestId, Boolean enable);

    /**
     *指令-获取增强图传设置状态
     * @param nestId
     * @return
     */
    Boolean getTransmission(String nestId);
}
