package com.imapcloud.nest.v2.web.transformer;

import com.imapcloud.nest.v2.service.dto.in.UavVirtualControlInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosUavCodeInDTO;
import com.imapcloud.nest.v2.service.dto.out.CaacCloudUavOutDTO;
import com.imapcloud.nest.v2.service.dto.out.CameraParamsOutDTO;
import com.imapcloud.nest.v2.service.dto.out.RtkInfoOutDTO;
import com.imapcloud.nest.v2.web.vo.req.*;
import com.imapcloud.nest.v2.web.vo.resp.CaacCloudUavRespVO;
import com.imapcloud.nest.v2.web.vo.resp.CameraParamsRespVO;
import com.imapcloud.nest.v2.web.vo.resp.RtkInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosUavCodeTransformer.java
 * @Description UosUavCodeTransformer
 * @createTime 2022年07月08日 17:03:00
 */
@Mapper(componentModel = "spring")
public interface UosUavCodeTransformer {

    UosUavCodeTransformer INSTANCES = Mappers.getMapper(UosUavCodeTransformer.class);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.UavOneKeyTakeOffInDTO transform(UavOneKeyTakeOffVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.UavControlInDTO transform(UavControlVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.UavMissionControlInDTO transform(UavMissionControlVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.UavCalibrationCompassInDTO transform(UavCalibrationCompassVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.UavG900LiveSourceInDTO transform(UavG900LiveSourceVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.UavVsInfraredInfoInDTO transform(UavVsInfraredInfoVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.UavChargeSwitchInDTO transform(UavChargeSwitchVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.UavSwitchInDTO transform(UavSwitchVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.UavLandControlInDTO transform(UavLandControlVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.NestSysControlInDTO transform(NestSysControlVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.SetUavMaxFlyAltInDTO transform(SetUavMaxFlyAltVO vo);



    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.CommonControlInDTO transform(GetUavMaxFlyAltVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.SetRthAltInDTO transform(SetRthAltVO vo);


    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.CommonControlInDTO transform(GetUavRTHAltVO vo);


    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.SetUavFlyLongestRadiusInDTO transform(SetUavFlyLongestRadiusVO vo);


    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.CommonControlInDTO transform(GetUavFlyLongestRadiusVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.SetUavBehaviorInDTO transform(SetUavBehaviorVO vo);


    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.IntellectShutdownInDTO transform(IntellectShutdownVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.CommonControlInDTO transform(GetUaxBehaviorVO vo);


    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.CommonControlInDTO transform(GetIntellectShutdownVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.IntellectGoHomeInDTO transform(IntellectGoHomeVO vo);


    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.CommonControlInDTO transform(GetRtkEnableVO vo);



    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.CommonControlInDTO transform(SwitchRtkVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.CommonControlInDTO transform(GetRtkTypeVO vo);
    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.SetRtkTypeInDTO transform(SetRtkTypeVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.SetRtkInfoInDTO transform(SetRtkInfoVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.CommonControlInDTO transform(GetRtkInfoVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.SetRtkExpireTimeInDTO transform(SetRtkExpireTimeVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.CommonControlInDTO transform(GetRtkExpireTimeVO vo);


    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.CommonControlInDTO transform(DRtkPowerSwitchVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.UavDjiLoginControlInDTO transform(UavDjiLoginControlVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.UavCameraControlInDTO transform(UavCameraControlVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.UavGimbalControlInDTO transform(UavGimbalControlVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.UavZoomControlInDTO transform(UavZoomControlVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UavVirtualControlInDTO transform(UavVirtualControlVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.UavSwitchControlInDTO transform(UavSwitchControlVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    RtkInfoVO transform(RtkInfoOutDTO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.UavCameraParamControlInDTO transform(UavCameraParamControlVO vo);

    /**
     * 转换出口
     * @param outDTO
     * @return
     */
    CameraParamsRespVO transform(CameraParamsOutDTO outDTO);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.SetCameraInfraredColorInDTO transform(SetCameraInfraredColorReqVO vo);

    /**
     * 转换入口
     * @param vo
     * @return
     */
    UosUavCodeInDTO.InfraredTestTempeInDTO transform(InfraredTestTempeReqVO vo);

    List<CaacCloudUavRespVO> transform(List<CaacCloudUavOutDTO> dto);

}
