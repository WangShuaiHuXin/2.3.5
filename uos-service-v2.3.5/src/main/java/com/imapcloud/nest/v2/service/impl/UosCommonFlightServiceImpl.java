package com.imapcloud.nest.v2.service.impl;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.UosCommonFlightService;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.rc.RcManagerCf;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosCommonFlightServiceImpl.java
 * @Description UosCommonFlightServiceImpl
 * @createTime 2022年08月18日 15:29:00
 */
@Service
public class UosCommonFlightServiceImpl implements UosCommonFlightService {

    @Resource
    private BaseNestService baseNestService;

    /**
     * 空中回巢
     *
     * @param nestId
     * @return
     */
    @Override
    public boolean flightBack(String nestId) {
        if (nestId == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXECUTE_AERIAL_NESTING_ACTION_NESTID_CANNOT_BE_EMPTY.getContent()));
        }
        ComponentManager cm = this.baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            throw new BusinessException(String.format("%s-机巢离线",nestId));
        }

        RcManagerCf rcManagerCf = cm.getRcManagerCf();
        MqttResult<NullParam> res = rcManagerCf.flightBack();
        if (!res.isSuccess()) {
            throw new BusinessException(String.format("空中回巢失败-%s",res.getMsg()));
        }
        return Boolean.TRUE;
    }
}
