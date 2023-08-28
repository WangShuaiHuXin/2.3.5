package com.imapcloud.nest.v2.service.impl;

import com.imapcloud.nest.utils.DistanceUtil;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.po.in.CpsMissionInPO;
import com.imapcloud.nest.v2.dao.po.out.CpsMissionOutPO;
import com.imapcloud.nest.v2.manager.cps.CpsMissionManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.CpsMissionService;
import com.imapcloud.nest.v2.service.dto.in.CpsMissionInDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseNestInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.CpsMissionOutDTO;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
public class CpsMissionServiceImpl implements CpsMissionService {

    public final Integer MAX_DISTANCE = Integer.valueOf("20");

    @Resource
    private CpsMissionManager cpsMissionManager;

    @Resource
    private BaseNestService baseNestService;

    @Override
    public Boolean setAlternateLanding(CpsMissionInDTO.CpsMissionLandingInDTO cpsMissionLandingInDTO) {
        //检查基站是否距离备降点超过20M
        if (checkDistance(cpsMissionLandingInDTO.getNestId(), cpsMissionLandingInDTO.getLongitude(), cpsMissionLandingInDTO.getLatitude())) {
            throw new BusinessException(MessageEnum.GEOAI_UOS_CPSMISSIONSERVICEIMPL_001.getContent());
        }
        CpsMissionInPO.CpsMissionLandingInPO inPO = new CpsMissionInPO.CpsMissionLandingInPO();
        inPO.setNestId(cpsMissionLandingInDTO.getNestId());
        inPO.setLatitude(cpsMissionLandingInDTO.getLatitude());
        inPO.setLongitude(cpsMissionLandingInDTO.getLongitude());
        inPO.setAltitude(cpsMissionLandingInDTO.getAltitude());
        inPO.setEnable(cpsMissionLandingInDTO.getEnable());
        //设置备降点坐标
        MqttResult<NullParam> res = cpsMissionManager.setAlternateLandingPosition(inPO);
        if (!res.isSuccess()) {
            throw new BusinessException(res.getMsg());
        }
        //设置前往备降点高度
        res = cpsMissionManager.setAlternateLandingAltitude(inPO);
        if (!res.isSuccess()) {
            throw new BusinessException(res.getMsg());
        }
        //设置开始自动前往备降点功能
        res = cpsMissionManager.setAlternateLandingStatus(cpsMissionLandingInDTO.getNestId(), inPO.getEnable());
        if (!res.isSuccess()) {
            throw new BusinessException(res.getMsg());
        }
        return true;
    }

    @Override
    public Boolean setAlternateLandingStatus(CpsMissionInDTO.CpsMissionLandingStatusInDTO inDTO) {
        checkNestInfo(inDTO.getNestId());
        MqttResult<NullParam> res = cpsMissionManager.setAlternateLandingStatus(inDTO.getNestId(), inDTO.getEnable());
        if (!res.isSuccess()) {
            throw new BusinessException(res.getMsg());
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean setAlternateLandingForward(CpsMissionInDTO.CpsMissionLandingInDTO inDTO) {
        checkNestInfo(inDTO.getNestId());
        //检查是否已设置备降点信息
        MqttResult<CpsMissionOutPO.CpsMissionEnableOutPO> alternateLandingEnable = cpsMissionManager.getAlternateLandingEnable(inDTO.getNestId());
        if (!alternateLandingEnable.isSuccess()) {
            throw new BusinessException(alternateLandingEnable.getMsg());
        }
        if (!alternateLandingEnable.getRes().enable) {
            throw new BusinessException(MessageEnum.GEOAI_UOS_CPSMISSIONSERVICEIMPL_002.getContent());
        }
        MqttResult<NullParam> res = cpsMissionManager.setAlternateLandingForward(inDTO.getNestId(), inDTO.getAltitude());
        if (!res.isSuccess()) {
            throw new BusinessException(res.getMsg());
        }
        return Boolean.TRUE;
    }


    @Override
    public CpsMissionOutDTO.CpsMissionAlternateInfoOutDTO getAlternateLandingInfo(String nestId) {
        checkNestInfo(nestId);
        //获取设置的备降点经纬度
        MqttResult<CpsMissionOutPO.CpsMissionPositionOutPO> res1 = cpsMissionManager.getAlternateLandingPosition(nestId);
        if (!res1.isSuccess()) {
            throw new BusinessException(res1.getMsg());
        }
        //获取设置的备降点高度
        MqttResult<CpsMissionOutPO.CpsMissionAltitudeOutPO> res2 = cpsMissionManager.getAlternateLandingAltitude(nestId);
        if (!res2.isSuccess()) {
            throw new BusinessException(res2.getMsg());
        }
        //获取是否开启备降点功能
        MqttResult<CpsMissionOutPO.CpsMissionEnableOutPO> res3 = cpsMissionManager.getAlternateLandingEnable(nestId);
        if (!res3.isSuccess()) {
            throw new BusinessException(res3.getMsg());
        }
        CpsMissionOutDTO.CpsMissionAlternateInfoOutDTO outDTO = new CpsMissionOutDTO.CpsMissionAlternateInfoOutDTO();
        CpsMissionOutPO.CpsMissionPositionOutPO positionOutPO = res1.getRes();
        if (!ObjectUtils.isEmpty(positionOutPO)) {
            outDTO.setLatitude(positionOutPO.getLatitude());
            outDTO.setLongitude(positionOutPO.getLongitude());
        }
        CpsMissionOutPO.CpsMissionAltitudeOutPO altitudeOutPO = res2.getRes();
        if (!ObjectUtils.isEmpty(altitudeOutPO)) {
            outDTO.setAltitude(altitudeOutPO.getAltitude());
        }
        CpsMissionOutPO.CpsMissionEnableOutPO enableOutPO = res3.getRes();
        if (!ObjectUtils.isEmpty(enableOutPO)) {
            outDTO.setEnable(enableOutPO.getEnable());
        }
        return outDTO;
    }

    public Boolean checkDistance(String nestId, Double lng2, Double lat2) {
        BaseNestInfoOutDTO baseNestInfo = baseNestService.getBaseNestInfo(nestId);
        if (ObjectUtils.isEmpty(baseNestInfo)) {
            throw new BusinessException(MessageEnum.GEOAI_UOS_CPSMISSIONSERVICEIMPL_003.getContent());
        }
        if (ObjectUtils.isEmpty(baseNestInfo.getLatitude()) || ObjectUtils.isEmpty(baseNestInfo.getLongitude())) {
            throw new BusinessException(MessageEnum.GEOAI_UOS_CPSMISSIONSERVICEIMPL_004.getContent());
        }
        BigDecimal distanceEarth = DistanceUtil.getDistanceEarth(baseNestInfo.getLongitude(), baseNestInfo.getLatitude(), lng2, lat2);
        return distanceEarth.intValue() > MAX_DISTANCE;
    }

    public void checkNestInfo(String nestId) {
        BaseNestInfoOutDTO baseNestInfo = baseNestService.getBaseNestInfo(nestId);
        if (ObjectUtils.isEmpty(baseNestInfo)) {
            throw new BusinessException(MessageEnum.GEOAI_UOS_CPSMISSIONSERVICEIMPL_005.getContent());
        }
    }

    @Override
    public Double getAlternateAltitudeInfo(String nestId) {
        checkNestInfo(nestId);
        //获取设置的备降点高度
        MqttResult<CpsMissionOutPO.CpsMissionAltitudeOutPO> res2 = cpsMissionManager.getAlternateLandingAltitude(nestId);
        if (!res2.isSuccess()) {
            throw new BusinessException(res2.getMsg());
        }
        return res2.getRes().getAltitude();
    }
}
