package com.imapcloud.nest.v2.manager.cps.impl;

import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.sdk.CommonNestState;
import com.imapcloud.nest.sdk.CommonNestStateFactory;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.po.in.AccessoryInPO;
import com.imapcloud.nest.v2.dao.po.out.AccessoryOutPO;
import com.imapcloud.nest.v2.manager.cps.AbstractComponentManager;
import com.imapcloud.nest.v2.manager.cps.CpsAccessoryManager;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.accessory.AccessoryManagerCf;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.accessory.AccessoryAuthStatusOutPo;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.entity.NestState;
import com.imapcloud.sdk.utils.JSONUtil;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CpsAccessoryManagerImpl extends AbstractComponentManager implements CpsAccessoryManager {
    @Resource
    private BaseNestManager baseNestManager;


    @Override
    public String getUuid(String nestId) {
        return baseNestManager.getUuidByNestId(nestId);
    }

    public AccessoryManagerCf getAccessoryManagerCf(String nestId) {
        ComponentManager componentManager = ComponentManagerFactory.getInstance(getUuid(nestId));
        AccessoryManagerCf accessoryManagerCf = componentManager.getAccessoryManagerCf();
        return accessoryManagerCf;
    }

    public void checkState(String nestId) {
        if (StringUtil.isEmpty(nestId)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAMERAMANAGERIMPL_001.getContent()));
        }
        CommonNestState nestStateManager = CommonNestStateFactory.getInstance(getUuid(nestId));
        if (ObjectUtils.isEmpty(nestStateManager)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAMERAMANAGERIMPL_002.getContent()));
        }
        NestState nestState = nestStateManager.getNestState(AirIndexEnum.DEFAULT);
        if (ObjectUtils.isEmpty(nestState)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAMERAMANAGERIMPL_001.getContent()));
        }
        Boolean connected = nestState.getAircraftConnected();
        if (!connected) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAMERAMANAGERIMPL_003.getContent()));
        }
    }

    @Override
    public AccessoryOutPO.AccessoryAuthOutPO accessoryAuthStatus(String nestId) {
        checkState(nestId);
        AccessoryManagerCf accessoryManagerCf = getAccessoryManagerCf(nestId);
        MqttResult<AccessoryAuthStatusOutPo> lteAhtuStatus = accessoryManagerCf.getLteAhtuStatus();
        AccessoryOutPO.AccessoryAuthOutPO outPO = new AccessoryOutPO.AccessoryAuthOutPO();
        outPO.setFlag(false);
        if (!lteAhtuStatus.isSuccess()) {
            outPO.setMsg(lteAhtuStatus.getMsg());
            return outPO;
        }
        AccessoryAuthStatusOutPo param = lteAhtuStatus.getRes();
        if (ObjectUtils.isEmpty(param)) {
            return outPO;
        }
        outPO.setFlag(true);
        outPO.setIsLTEAuthenticationAvailable(param.getIsLTEAuthenticationAvailable());
        outPO.setIsLTEAuthenticated(param.getIsLTEAuthenticated());
        outPO.setLTEAuthenticatedPhoneAreaCode(param.getLTEAuthenticatedPhoneAreaCode());
        outPO.setLTEAuthenticatedPhoneNumber(param.getLTEAuthenticatedPhoneNumber());
        outPO.setLTELastAuthenticatedTime(param.getLTELastAuthenticatedTime());
        outPO.setLTEAuthenticatedRemainingFatalism(param.getLTEAuthenticatedRemainingTime());
        return outPO;
    }

    @Override
    public Boolean accessorySendCaptcha(AccessoryInPO.AccessoryCaptchaInPO inPO) {
        String nestId = inPO.getNestId();
        checkState(nestId);
        AccessoryManagerCf accessoryManagerCf = getAccessoryManagerCf(nestId);
        log.info("发送验证码回调，{},{} ", inPO.getLteaPhoneNumber(), inPO.getLteaAreaCode());
        MqttResult<NullParam> nullParamMqttResult = accessoryManagerCf.sendCaptcha(inPO);
        if (!nullParamMqttResult.isSuccess()) {
            throw new BusinessException(nullParamMqttResult.getMsg());
        }
        return true;
    }

    @Override
    public Boolean sendCertification(AccessoryInPO.AccessoryCaptchaInPO inPO) {
        String nestId = inPO.getNestId();
        checkState(nestId);
        log.info("实名认证回调，phone:{},area:{},certificationCode:{} ", inPO.getLteaPhoneNumber(), inPO.getLteaAreaCode(),inPO.getVerificationCode());
        AccessoryManagerCf accessoryManagerCf = getAccessoryManagerCf(nestId);
        MqttResult<NullParam> nullParamMqttResult = accessoryManagerCf.sendCertification(inPO);
        if (!nullParamMqttResult.isSuccess()) {
            throw new BusinessException(nullParamMqttResult.getMsg());
        }
        return true;
    }

    @Override
    public Boolean setTransmission(String nestId, Boolean enable) {
        checkState(nestId);
        AccessoryManagerCf accessoryManagerCf = getAccessoryManagerCf(nestId);
        MqttResult<NullParam> nullParamMqttResult = accessoryManagerCf.setTransmission(enable);
        if (!nullParamMqttResult.isSuccess()) {
            throw new BusinessException(nullParamMqttResult.getMsg());
        }
        return true;
    }

    @Override
    public Boolean getTransmission(String nestId) {
        checkState(nestId);
        AccessoryManagerCf accessoryManagerCf = getAccessoryManagerCf(nestId);
        MqttResult<AccessoryOutPO.AccessoryEnableOutPO> mqttResult = accessoryManagerCf.getTransmission();
        if (!mqttResult.isSuccess()) {
            throw new BusinessException(mqttResult.getMsg());
        }
        Boolean enable = mqttResult.getRes().getEnable();
        return enable;
    }
}
