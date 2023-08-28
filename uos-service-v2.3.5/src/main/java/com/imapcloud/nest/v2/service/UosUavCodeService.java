package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.UavVirtualControlInDTO;
import com.imapcloud.nest.v2.service.dto.in.UosUavCodeInDTO;
import com.imapcloud.nest.v2.service.dto.out.CameraParamsOutDTO;
import com.imapcloud.nest.v2.service.dto.out.RtkInfoOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.IntellectShutdownRespVO;
import com.imapcloud.sdk.manager.rc.entity.ElseWhereParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosUavCodeService.java
 * @Description UosUavCodeService
 * @createTime 2022年10月31日 16:11:00
 */
public interface UosUavCodeService {


    public Boolean batchOneKeyTakeOff(List<UosUavCodeInDTO.UavOneKeyTakeOffInDTO> uavOneKeyTakeOffInDTOS);

    public Boolean rtkReconnect( UosUavCodeInDTO.UavControlInDTO inDTO);


    public Boolean rcPair( UosUavCodeInDTO.UavControlInDTO inDTO);


    public Boolean rcOnOff( UosUavCodeInDTO.UavControlInDTO inDTO);


    public Boolean rcSwitch( UosUavCodeInDTO.UavControlInDTO inDTO);


    public Boolean stopAllProcess( UosUavCodeInDTO.UavControlInDTO inDTO);


    public Boolean calibrationCompassSwitch(UosUavCodeInDTO.UavCalibrationCompassInDTO inDTO);


    public Boolean g900SwitchLiveSource( UosUavCodeInDTO.UavG900LiveSourceInDTO inDTO);


    public Double setVsInfraredInfo(UosUavCodeInDTO.UavVsInfraredInfoInDTO inDTO);

    public Boolean s100UavChargeSwitch( UosUavCodeInDTO.UavChargeSwitchInDTO inDTO);

    public Boolean s100UavSwitch( UosUavCodeInDTO.UavSwitchInDTO inDTO);


    public Boolean againLand( UosUavCodeInDTO.UavLandControlInDTO inDTO);

    public ElseWhereParam oneKeyGoHome(UosUavCodeInDTO.UavLandControlInDTO inDTO);

    public Boolean forceLand( UosUavCodeInDTO.UavLandControlInDTO inDTO);

    public Boolean autoLand( UosUavCodeInDTO.UavLandControlInDTO inDTO);

    public Boolean formatUavSdCard( UosUavCodeInDTO.NestSysControlInDTO inDTO);

    public Boolean clearDjiCacheFile( UosUavCodeInDTO.NestSysControlInDTO inDTO);

    public Boolean setUavMaxFlyAlt( UosUavCodeInDTO.SetUavMaxFlyAltInDTO inDTO);

//    public Integer getUavMaxFlyAlt(GetUavMaxFlyAlt getUavMaxFlyAltVO);

    public Integer getUavMaxFlyAlt(UosUavCodeInDTO.CommonControlInDTO getUavMaxFlyAltVO);

    public Boolean setUavRTHAlt( UosUavCodeInDTO.SetRthAltInDTO inDTO);

//    public Integer getUavRTHAlt(GetUavRTHAltVO inDTO);

    public Integer getUavRTHAlt(UosUavCodeInDTO.CommonControlInDTO inDTO);


    public Boolean setUavFlyLongestRadius( UosUavCodeInDTO.SetUavFlyLongestRadiusInDTO inDTO);

//    public Integer getUavFlyLongestRadius(GetUavFlyLongestRadiusVO inDTO);

    public Integer getUavFlyLongestRadius(UosUavCodeInDTO.CommonControlInDTO inDTO);

    public Boolean setUavBehavior( UosUavCodeInDTO.SetUavBehaviorInDTO inDTO);

//    public String getUaxBehavior(GetUaxBehaviorVO getUaxBehaviorVO);

    public String getUaxBehavior(UosUavCodeInDTO.CommonControlInDTO getUaxBehaviorVO);

    public Boolean setLowBatteryIntellectShutdown( UosUavCodeInDTO.IntellectShutdownInDTO inDTO) ;

//    public IntellectShutdownRespVO getLowBatteryIntellectShutdown(GetIntellectShutdownVO inDTO);

    public IntellectShutdownRespVO getLowBatteryIntellectShutdown(UosUavCodeInDTO.CommonControlInDTO inDTO);

    public Boolean setLowBatteryIntellectGoHome( UosUavCodeInDTO.IntellectGoHomeInDTO inDTO);

//    public Boolean getRtkEnable(GetRtkEnableVO inDTO);

    public Boolean getRtkEnable(UosUavCodeInDTO.CommonControlInDTO inDTO);

//    public Boolean switchRtk( SwitchRtkVO inDTO);
//
//    public Integer getRtkType(GetRtkTypeVO inDTO);
//
//    public Boolean setRtkType( SetRtkTypeVO inDTO);
//
//    public Boolean setRtkInfo( SetRtkInfoVO inDTO);

    public Boolean switchRtk( UosUavCodeInDTO.CommonControlInDTO inDTO);

    public Integer getRtkType(UosUavCodeInDTO.CommonControlInDTO inDTO);

    public Boolean setRtkType( UosUavCodeInDTO.SetRtkTypeInDTO inDTO);

    public Boolean setRtkInfo( UosUavCodeInDTO.SetRtkInfoInDTO inDTO);

//    public RtkInfoVO getRtkInfo(GetRtkInfoVO getRtkInfoVO);

    public RtkInfoOutDTO getRtkInfo(UosUavCodeInDTO.CommonControlInDTO getRtkInfoVO);

//    public Boolean setRtkExpireTime( SetRtkExpireTimeVO inDTO);
//
//    public String getRtkExpireTime(GetRtkExpireTimeVO inDTO);
//
//    public Boolean dRtkPowerSwitch(DRtkPowerSwitchVO inDTO);

    public Boolean setRtkExpireTime( UosUavCodeInDTO.SetRtkExpireTimeInDTO inDTO);

    public String getRtkExpireTime(UosUavCodeInDTO.CommonControlInDTO inDTO);

    public Boolean dRtkPowerSwitch(UosUavCodeInDTO.CommonControlInDTO inDTO);

    public Boolean switchVideoCaptions(UosUavCodeInDTO.UavSwitchControlInDTO inDTO);

    public Boolean getVideoCaptionsState(UosUavCodeInDTO.UavControlInDTO inDTO);

    public Boolean returnToHome(UosUavCodeInDTO.UavControlInDTO inDTO);

    public Boolean originalRoadGoHome(UosUavCodeInDTO.UavControlInDTO inDTO);

    public Boolean flyBack(UosUavCodeInDTO.UavControlInDTO inDTO);

    public Boolean resetNestCameraSettings(UosUavCodeInDTO.UavControlInDTO inDTO);

    public Boolean pauseMission(UosUavCodeInDTO.UavControlInDTO inDTO);

    public Boolean continueMission(UosUavCodeInDTO.UavMissionControlInDTO inDTO);

    public Boolean getDjiLoginStatus(UosUavCodeInDTO.UavControlInDTO inDTO);

    public Boolean loginDjiAccount(UosUavCodeInDTO.UavDjiLoginControlInDTO inDTO);

    public Boolean m300SwitchCamera(UosUavCodeInDTO.UavCameraControlInDTO inDTO);

    public Boolean aircraftRePush(UosUavCodeInDTO.UavControlInDTO inDTO);

    public Boolean resetCameraAngle(UosUavCodeInDTO.UavControlInDTO inDTO);

    public Boolean controlGimbal(UosUavCodeInDTO.UavGimbalControlInDTO inDTO);

    public Boolean startPhotograph(UosUavCodeInDTO.UavControlInDTO inDTO);

    public Boolean setZoomRatio(UosUavCodeInDTO.UavZoomControlInDTO inDTO);

    public Boolean batchOpenVirtualStick(List<UosUavCodeInDTO.UavControlInDTO> inDTOList);

    public Boolean batchControlVirtualStickV2(UavVirtualControlInDTO inDTO);

    public CameraParamsOutDTO getCameraParam(UosUavCodeInDTO.UavCameraParamControlInDTO inDTO);

    public Boolean setCameraInfraredColor(UosUavCodeInDTO.SetCameraInfraredColorInDTO inDTO);

    public BigDecimal infraredAreaOrPointTestTemperature(UosUavCodeInDTO.InfraredTestTempeInDTO inDTO);


}
