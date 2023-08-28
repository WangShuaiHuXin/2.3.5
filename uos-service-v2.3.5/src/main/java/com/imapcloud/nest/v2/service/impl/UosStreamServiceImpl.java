package com.imapcloud.nest.v2.service.impl;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.UosStreamService;
import com.imapcloud.nest.v2.service.dto.out.StreamOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.general.GeneralManagerCf;
import com.imapcloud.sdk.manager.general.enums.PushStreamMode;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosStreamServiceImpl.java
 * @Description UosStreamServiceImpl
 * @createTime 2022年08月18日 16:12:00
 */
@Service
public class UosStreamServiceImpl implements UosStreamService {

    @Resource
    private BaseNestService baseNestService;


    /**
     * 设置推流模式
     * @param nestId
     * @param mode
     * @return
     */
    @Override
    public boolean setStream(String nestId, Integer mode , Integer uavWhich) {
        if (nestId == null || mode == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_NESTID_AND_MODE_EXECUTE_SET_PUSH_MODE_ACTION.getContent()));
        }
        ComponentManager cm = this.baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            throw new BusinessException(String.format("%s-机巢离线",nestId));
        }
        GeneralManagerCf gnManagerCf = cm.getGeneralManagerCf();
        MqttResult<NullParam> res = gnManagerCf.setPushStreamMode(PushStreamMode.getInstance(mode)
                ,Objects.isNull(uavWhich)? AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(uavWhich));
        if (!res.isSuccess()) {
            throw new BusinessException(String.format("设置推流模式失败-%s-%s-%d",res.getMsg(),nestId,mode));
        }
        return Boolean.TRUE;
    }

    /**
     * 获取推流
     * @param nestId
     * @return
     */
    @Override
    public StreamOutDTO getStream(String nestId,Integer uavWhich) {
        StreamOutDTO outDTO = null;
        if (nestId == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_EMPTY_NESTID_EXECUTE_GET_PUSH_MODE_ACTION.getContent()));
        }
        ComponentManager cm = this.baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            throw new BusinessException(String.format("%s-机巢离线",nestId));
        }
        GeneralManagerCf gnManagerCf = cm.getGeneralManagerCf();
        MqttResult<PushStreamMode> res = gnManagerCf.getPushStreamMode(Objects.isNull(uavWhich)? AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(uavWhich));
        if (!res.isSuccess()) {
            throw new BusinessException(String.format("获取推流模式失败-%s-%s",res.getMsg(),nestId));
        }
        PushStreamMode pushStreamMode = res.getRes();
        Optional.ofNullable(pushStreamMode)
                .orElseThrow(()->new BusinessException(String.format("获取推流模式失败-%s-%s",res.getMsg(),nestId)));
        outDTO = StreamOutDTO.builder().nestId(nestId)
                .mode(pushStreamMode.getValue())
                .modeStr(pushStreamMode.name())
                .build();
        return outDTO;
    }
}
