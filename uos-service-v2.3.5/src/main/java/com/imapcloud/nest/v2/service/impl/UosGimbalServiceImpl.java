package com.imapcloud.nest.v2.service.impl;

import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.UosGimbalService;
import com.imapcloud.nest.v2.service.dto.in.GimbalInDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.camera.CameraManagerCf;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosGimbalServiceImpl.java
 * @Description UosGimbalServiceImpl
 * @createTime 2022年08月19日 18:11:00
 */
@Slf4j
@Service
public class UosGimbalServiceImpl implements UosGimbalService {

    @Resource
    private BaseNestService baseNestService;

    /**
     * 云台控制
     *
     * @param gimbalInDTO
     * @return
     */
    @Override
    public Boolean gimbal(GimbalInDTO gimbalInDTO) {
        if (gimbalInDTO.getPitchAngle() == null && gimbalInDTO.getYamAngle() == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXECUTE_HEAD_ADJUSTMENT_ACTION_PITCHANGLE_AND_YAMANGLE_CANNOT_BE_EMPTY.getContent()));
        }
        ComponentManager cm = this.baseNestService.getComponentManagerByNestId(gimbalInDTO.getNestId());
        if (cm == null) {
            throw new BusinessException(String.format("%s-机巢离线",gimbalInDTO.getNestId()));
        }
        CameraManagerCf cameraManagerCf = cm.getCameraManagerCf();
        MqttResult<NullParam> res = cameraManagerCf.resetCameraAngleOpposite(gimbalInDTO.getPitchAngle()
                ,gimbalInDTO.getYamAngle()
                , Objects.isNull(gimbalInDTO.getWhich())? AirIndexEnum.DEFAULT:AirIndexEnum.getInstance(gimbalInDTO.getWhich()));
        if (!res.isSuccess()) {
            throw new BusinessException(String.format("执行云台调整动作失败-%s"
                    ,res.getMsg()));
        }
        return Boolean.TRUE;
    }
}

