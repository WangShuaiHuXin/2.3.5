package com.imapcloud.nest.v2.manager.cps;

import com.imapcloud.nest.v2.dao.po.in.AccessoryInPO;
import com.imapcloud.nest.v2.dao.po.out.AccessoryOutPO;

public interface CpsAccessoryManager {
    /**
     * 获取认证状态
     * @param nestId
     * @return
     */
    AccessoryOutPO.AccessoryAuthOutPO accessoryAuthStatus(String nestId);

    /**
     * 发送验证码
     * @param inPO
     * @return
     */
    Boolean accessorySendCaptcha(AccessoryInPO.AccessoryCaptchaInPO inPO);

    /**
     * 发起实名认证指令
     * @param inPO
     * @return
     */
    Boolean sendCertification(AccessoryInPO.AccessoryCaptchaInPO inPO);

    /**
     * 指令-设置增强图传状态
     * @param nestId
     * @param enable
     * @return
     */
    Boolean setTransmission(String nestId, Boolean enable);

    /**
     * 指令-获取增强图传设置状态
     * @param nestId
     * @return
     */
    Boolean getTransmission(String nestId);
}
