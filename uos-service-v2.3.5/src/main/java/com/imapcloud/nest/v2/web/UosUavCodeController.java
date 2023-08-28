package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.GimbalAutoFollow;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.common.annotation.Trace;
import com.imapcloud.nest.v2.service.UosUavCodeService;
import com.imapcloud.nest.v2.service.dto.out.CameraParamsOutDTO;
import com.imapcloud.nest.v2.service.dto.out.RtkInfoOutDTO;
import com.imapcloud.nest.v2.web.transformer.UosUavCodeTransformer;
import com.imapcloud.nest.v2.web.vo.req.*;
import com.imapcloud.nest.v2.web.vo.resp.CameraParamsRespVO;
import com.imapcloud.nest.v2.web.vo.resp.IntellectShutdownRespVO;
import com.imapcloud.nest.v2.web.vo.resp.RtkInfoVO;
import com.imapcloud.sdk.manager.rc.entity.ElseWhereParam;
import com.imapcloud.sdk.manager.system.SystemManagerCode;
import com.imapcloud.sdk.pojo.constant.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 控制无人机指令
 */
@ApiSupport(author = "wangmin@geoai.com", order = 3)
@Api(value = "无人机控制指令API(新)", tags = "无人机控制指令API（新）")
@RequestMapping("v2/uav/code")
@RestController
public class UosUavCodeController {

    @Resource
    private UosUavCodeService uosUavCodeService;


    @NestCodeRecord(Constant.EACC_RC_TAKE_OFF)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "批量一键起飞", notes = "无人机控制,危险动作，执行此动作之前先确认机巢处于开启状态并且附近无人")
    @PostMapping("/batch/one/key/take/off")
    public Result<Boolean> batchOneKeyTakeOff(@RequestBody @Valid List<UavOneKeyTakeOffVO> vos) {
        this.uosUavCodeService.batchOneKeyTakeOff(vos.stream().map(UosUavCodeTransformer.INSTANCES::transform).collect(Collectors.toList()));
        return Result.ok();
    }

    @NestCodeRecord(Constant.EACC_RTK_RECONNECT)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "RTK重连", notes = "无人机控制")
    @PostMapping("/rtk/reconnect")
    public Result<Boolean> rtkReconnect(@RequestBody @Valid  UavControlVO vo) {
        this.uosUavCodeService.rtkReconnect(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_RC_VIRTUAL_STICK_PAIR)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "遥控器对频", notes = "无人机控制")
    @PostMapping("/rc/pair")
    public Result<Boolean> rcPair(@RequestBody @Valid  UavControlVO vo) {
        this.uosUavCodeService.rcPair(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_RC_SWITCH_DEVICES)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "遥控器开关机", notes = "无人机控制")
    @PostMapping("/rc/on/off")
    public Result<Boolean> rcOnOff(@RequestBody @Valid  UavControlVO vo) {
        this.uosUavCodeService.rcOnOff(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_RC_SWITCH_MODE)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "遥控器切挡", notes = "无人机控制")
    @PostMapping("/rc/switch")
    public Result<Boolean> rcSwitch(@RequestBody @Valid  UavControlVO vo) {
        this.uosUavCodeService.rcSwitch(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.MISSION_MANAGER_C14)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "终止飞行", notes = "无人机控制，终止所有任务进程")
    @PostMapping("/stop/all/process")
    public Result<Boolean> stopAllProcess(@RequestBody @Valid  UavControlVO vo) {
        this.uosUavCodeService.stopAllProcess(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C44)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "磁罗盘校准", notes = "无人机控制")
    @PostMapping("/calibration/compass/switch")
    public Result<Boolean> calibrationCompassSwitch(@RequestBody @Valid  UavCalibrationCompassVO vo) {
        this.uosUavCodeService.calibrationCompassSwitch(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C51)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "G900切换视频源", notes = "无人机控制")
    @PostMapping("/g900/switch/live/source")
    public Result<Boolean> g900SwitchLiveSource(@RequestBody @Valid  UavG900LiveSourceVO vo) {
        this.uosUavCodeService.g900SwitchLiveSource(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }


    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C62)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "开关红外测温", notes = "无人机控制")
    @PostMapping("/set/vs/infrared/info")
    public Result<Double> setVsInfraredInfo(@RequestBody @Valid  UavVsInfraredInfoVO vo) {
        Double returnDouble = this.uosUavCodeService.setVsInfraredInfo(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(returnDouble);
    }

    @NestCodeRecord(Constant.AIRCRAFT_CHARGE_ON)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "S100无人机充电，断电", notes = "无人机控制")
    @PostMapping("/s100/uav/charge/switch")
    public Result<Boolean> s100UavChargeSwitch(@RequestBody @Valid  UavChargeSwitchVO vo) {
        //TODO 分割方法
        this.uosUavCodeService.s100UavChargeSwitch(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }


    @NestCodeRecord(Constant.AIRCRAFT_ON_MINI_V2)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "S100无人机开关机", notes = "无人机控制")
    @PostMapping("/s100/uav/switch")
    public Result<Boolean> s100UavSwitch(@RequestBody @Valid  UavSwitchVO vo) {
        //TODO
        this.uosUavCodeService.s100UavSwitch(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }


    @NestCodeRecord(Constant.EACC_RC_AGAIN_LAND)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "重新降落", notes = "降落控制")
    @PostMapping("/again/land")
    public Result<Boolean> againLand(@RequestBody @Valid  UavLandControlVO vo) {
        this.uosUavCodeService.againLand(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_RC_ELSEWHERE_GO_HOME)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "一键回巢", notes = "降落控制")
    @PostMapping("/one/key/go/home")
    public Result<String> oneKeyGoHome(@RequestBody @Valid  UavLandControlVO vo) {
        ElseWhereParam elseWhereParam = this.uosUavCodeService.oneKeyGoHome(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok("【一键回巢】启动成功，无人机将飞往{'homeLng':" + elseWhereParam.getHomeLng() + ",'homeLat':" + elseWhereParam.getHomeLat() + "}点");
    }

    @NestCodeRecord(Constant.EACC_RC_LAND_IN_PLACE)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "强制降落", notes = "降落控制")
    @PostMapping("/force/land")
    public Result<Boolean> forceLand(@RequestBody @Valid  UavLandControlVO vo) {
        this.uosUavCodeService.forceLand(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_RC_LAND_AUTO)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "自动（视觉降落）降落", notes = "降落控制")
    @PostMapping("/auto/land")
    public Result<Boolean> autoLand(@RequestBody @Valid  UavLandControlVO vo) {
        this.uosUavCodeService.autoLand(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }


    @NestCodeRecord(Constant.MEDIA_MANAGER_2_C203)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "无人机SD卡格式化", notes = "系统控制")
    @PostMapping("/format/uav/sd/card")
    public Result<Boolean> formatUavSdCard(@RequestBody @Valid  NestSysControlVO vo) {
        this.uosUavCodeService.formatUavSdCard(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(SystemManagerCode.SYSTEM_MANAGER_C12)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "清空大疆缓存文件", notes = "系统控制")
    @PostMapping("/clear/dji/cache/file")
    public Result<Boolean> clearDjiCacheFile(@RequestBody @Valid  NestSysControlVO vo) {
        this.uosUavCodeService.clearDjiCacheFile(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }


    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C4)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "设置无人机最大飞行高度", notes = "状态信息")
    @PostMapping("/set/max/fly/alt")
    public Result<Boolean> setUavMaxFlyAlt(@RequestBody @Valid  SetUavMaxFlyAltVO vo) {
        this.uosUavCodeService.setUavMaxFlyAlt(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C5)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "获取无人机最大飞行高度", notes = "状态信息")
    @GetMapping("/get/max/fly/alt")
    public Result<Integer> getUavMaxFlyAlt(GetUavMaxFlyAltVO vo) {
        Integer uavMaxFlyAlt = this.uosUavCodeService.getUavMaxFlyAlt(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(uavMaxFlyAlt);
    }

    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C2)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "设置无人机返航高度", notes = "状态信息")
    @PostMapping("/set/rth/alt")
    public Result<Boolean> setUavRTHAlt(@RequestBody @Valid  SetRthAltVO vo) {
        this.uosUavCodeService.setUavRTHAlt(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C3)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "获取无人机返航高度", notes = "状态信息")
    @GetMapping("/get/rth/alt")
    public Result<Integer> getUavRTHAlt(GetUavRTHAltVO vo) {
        Integer uavRTHAlt = this.uosUavCodeService.getUavRTHAlt(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(uavRTHAlt);
    }

    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C72)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "设置最大飞行半径", notes = "状态信息")
    @PostMapping("/set/fly/longest/radius")
    public Result<Boolean> setUavFlyLongestRadius(@RequestBody @Valid  SetUavFlyLongestRadiusVO vo) {
        this.uosUavCodeService.setUavFlyLongestRadius(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C73)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "获取最大飞行半径", notes = "状态信息")
    @GetMapping("/get/fly/longest/radius")
    public Result<Integer> getUavFlyLongestRadius(GetUavFlyLongestRadiusVO vo) {
        Integer uavFlyLongestRadius = this.uosUavCodeService.getUavFlyLongestRadius(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(uavFlyLongestRadius);
    }

    @NestCodeRecord(Constant.EACC_RC_SET_BEHAVIOR)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "设置无人机失控行为", notes = "状态信息")
    @PostMapping("/set/behavior")
    public Result<Boolean> setUavBehavior(@RequestBody @Valid  SetUavBehaviorVO vo) {
        this.uosUavCodeService.setUavBehavior(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_RC_GET_BEHAVIOR)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "获取无人机失控行为", notes = "状态信息")
    @GetMapping("/get/uax/behavior")
    public Result<String> getUaxBehavior(GetUaxBehaviorVO vo) {
        String str = this.uosUavCodeService.getUaxBehavior(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(str);
    }

    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C54)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "设置低电量智能关机", notes = "状态信息")
    @PostMapping("/set/low/battery/intellect/shutdown")
    public Result<Boolean> setLowBatteryIntellectShutdown(@RequestBody @Valid  IntellectShutdownVO vo) {
        this.uosUavCodeService.setLowBatteryIntellectShutdown(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C55)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "获取低电量智能关机", notes = "状态信息")
    @GetMapping("/get/low/battery/intellect/shutdown")
    public Result<IntellectShutdownRespVO> getLowBatteryIntellectShutdown(GetIntellectShutdownVO vo) {
        IntellectShutdownRespVO intellectShutdownRespVO = new IntellectShutdownRespVO();
        intellectShutdownRespVO = this.uosUavCodeService.getLowBatteryIntellectShutdown(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(intellectShutdownRespVO);
    }

    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C25)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "设置低电量智能返航", notes = "状态信息")
    @PostMapping("/set/low/battery/intellect/go/home")
    public Result<Boolean> setLowBatteryIntellectGoHome(@RequestBody @Valid  IntellectGoHomeVO vo) {
        this.uosUavCodeService.setLowBatteryIntellectGoHome(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_RTK_ENABLE)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "获取RTK是否开启状态", notes = "状态信息")
    @GetMapping("/get/rtk/enable")
    public Result<Boolean> getRtkEnable(GetRtkEnableVO vo) {
        Boolean rtkEnable = this.uosUavCodeService.getRtkEnable(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(rtkEnable);
    }

    @NestCodeRecord(Constant.EACC_RTK_OPEN)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "RKT开关", notes = "状态信息")
    @PostMapping("/switch/rtk")
    public Result<Boolean> switchRtk(@RequestBody @Valid  SwitchRtkVO vo) {
        this.uosUavCodeService.switchRtk(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_RTK_GET_TYPE)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "获取RTK类型", notes = "状态信息")
    @GetMapping("/get/rtk/type")
    public Result<Integer> getRtkType(GetRtkTypeVO vo) {
        Integer rtyType = this.uosUavCodeService.getRtkType(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(rtyType);
    }

    @NestCodeRecord(Constant.EACC_RTK_SET_TYPE)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "设置RTK类型", notes = "状态信息")
    @PostMapping("/set/rtk/type")
    public Result<Boolean> setRtkType(@RequestBody @Valid  SetRtkTypeVO vo) {
        this.uosUavCodeService.setRtkType(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_RTK_SET_ACCOUNT)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "设置RTK信息", notes = "状态信息")
    @PostMapping("/set/rtk/info")
    public Result<Boolean> setRtkInfo(@RequestBody @Valid  SetRtkInfoVO vo) {
        this.uosUavCodeService.setRtkInfo(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_RTK_GET_ACCOUNT)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "获取RTK信息", notes = "状态信息")
    @GetMapping("/get/rtk/info")
    public Result<RtkInfoVO> getRtkInfo(GetRtkInfoVO vo) {
        RtkInfoVO rtkInfoVO = new RtkInfoVO();
        RtkInfoOutDTO rtkInfoOutDTO = this.uosUavCodeService.getRtkInfo(UosUavCodeTransformer.INSTANCES.transform(vo));
        rtkInfoVO = UosUavCodeTransformer.INSTANCES.transform(rtkInfoOutDTO);
        return Result.ok(rtkInfoVO);
    }

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C60)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "设置RTK过期时间", notes = "状态信息")
    @PostMapping("/set/rtk/expire/time")
    public Result<Boolean> setRtkExpireTime(@RequestBody @Valid  SetRtkExpireTimeVO vo) {
        this.uosUavCodeService.setRtkExpireTime(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C60)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "获取RTK过期时间", notes = "状态信息")
    @GetMapping("/get/rtk/expire/time")
    public Result<String> getRtkExpireTime(GetRtkExpireTimeVO vo) {
        String str = this.uosUavCodeService.getRtkExpireTime(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(str);
    }

    @NestCodeRecord(Constant.DRTK_POWER_ON)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "DRTK开关", notes = "状态信息")
    @PostMapping("/drtk/power/switch")
    public Result<Boolean> dRtkPowerSwitch(DRtkPowerSwitchVO vo) {
        this.uosUavCodeService.dRtkPowerSwitch(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }


    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C45)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "视频字幕开关", notes = "视频字幕开关")
    @PostMapping("/switch/video/captions")
    public Result<Boolean> switchVideoCaptions(@RequestBody @Valid  UavSwitchControlVO vo) {
        this.uosUavCodeService.switchVideoCaptions(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C46)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "获取视频字幕开关", notes = "获取视频字幕开关")
    @Trace
    @GetMapping("/get/video/captions/state")
    public Result<Integer> getVideoCaptionsState(@Valid  UavControlVO vo) {
        Boolean bol = this.uosUavCodeService.getVideoCaptionsState(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(bol?1:0);
    }

    @NestCodeRecord(Constant.EACC_RC_RTH)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "一键返航", notes = "一键返航")
    @PostMapping("/return/to/home")
    public Result<Boolean> startReturnToHome(@RequestBody @Valid  UavControlVO vo) {
        this.uosUavCodeService.returnToHome(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_RC_ORIGINAL_GO_HOME)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "原路返航", notes = "原路返航")
    @PostMapping("/original/road/go/home")
    public Result<Boolean> originalRoadGoHome(@RequestBody @Valid  UavControlVO vo) {
        this.uosUavCodeService.originalRoadGoHome(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_RC_FLIGHT_BACK)
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "空中回巢", notes = "空中回巢")
    @PostMapping("/flyBack")
    public Result<Boolean> flyBack(@RequestBody @Valid  UavControlVO vo) {
        this.uosUavCodeService.flyBack(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C18)
    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "重置基站摄像机基本设置", notes = "如果基站挂载多个相机，则会重置当前使用的相机配置")
    @PostMapping("/cameras/reset")
    public Result<Boolean> resetNestCameraSettings(@RequestBody @Valid  UavControlVO vo) {
        this.uosUavCodeService.resetNestCameraSettings(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.MISSION_MANAGER_C10)
    @Trace
    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "暂停任务", notes = "暂停任务")
    @PostMapping("/pause/mission")
    public Result<Boolean> pauseMission(@RequestBody @Valid  UavControlVO vo) {
        this.uosUavCodeService.pauseMission(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @GimbalAutoFollow
    @NestCodeRecord(Constant.MISSION_MANAGER_C11)
    @Trace
    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "继续任务", notes = "继续任务")
    @PostMapping("/continue/mission")
    public Result<Boolean> continueMission(@RequestBody @Valid  UavMissionControlVO vo) {
        this.uosUavCodeService.continueMission(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "大疆登陆状态", notes = "大疆登陆状态")
    @Trace
    @GetMapping("/get/dji/login/status")
    public Result<Boolean> getDjiLoginStatus(@Valid  UavControlVO vo) {
        Boolean djiLoginStatus = this.uosUavCodeService.getDjiLoginStatus(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(djiLoginStatus);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "大疆登陆", notes = "大疆登陆")
    @Trace
    @PostMapping("/login/dji/account")
    public Result<Boolean> loginDjiAccount(@RequestBody @Valid  UavDjiLoginControlVO vo) {
        this.uosUavCodeService.loginDjiAccount(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C19)
    @ApiOperationSupport(author = "zhongtaibao@geoai.com", order = 1)
    @ApiOperation(value = "镜头切换", notes = "镜头切换")
    @GimbalAutoFollow
    @Trace
    @PostMapping("/m300/switch/camera")
    public Result<Boolean>  m300SwitchCamera(@RequestBody @Valid  UavCameraControlVO vo) {
        this.uosUavCodeService.m300SwitchCamera(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    /**
     * 飞机图传重新推流
     *
     * @param vo
     * @return
     */
    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C8)
    @ApiOperationSupport(author = "zhongtaibao@geoai.com", order = 1)
    @ApiOperation(value = "重新推流", notes = "重新推流")
    @Trace
    @PostMapping("/aircraft/re/push")
    public Result<Boolean> aircraftRePush(@RequestBody @Valid  UavControlVO vo) {
        this.uosUavCodeService.aircraftRePush(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @GimbalAutoFollow
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C40)
    @ApiOperationSupport(author = "zhongtaibao@geoai.com", order = 1)
    @ApiOperation(value = "归中", notes = "归中")
    @Trace
    @GetMapping("/reset/angle")
    public Result<Boolean> resetCameraAngle(@Valid  UavControlVO vo) {
        this.uosUavCodeService.resetCameraAngle(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @GimbalAutoFollow
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C38)
    @ApiOperationSupport(author = "zhongtaibao@geoai.com", order = 1)
    @ApiOperation(value = "云台调整绝对", notes = "云台调整绝对")
    @Trace
    @PostMapping("/control/gimbal")
    public Result<Boolean> controlGimbal(@RequestBody @Valid  UavGimbalControlVO vo) {
        this.uosUavCodeService.controlGimbal(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C35)
    @ApiOperationSupport(author = "zhongtaibao@geoai.com", order = 1)
    @ApiOperation(value = "变焦", notes = "变焦")
    @Trace
    @GetMapping("/set/ratio")
    public Result<Boolean> setZoomRatio(@Valid  UavZoomControlVO vo) {
        this.uosUavCodeService.setZoomRatio(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C20)
    @ApiOperationSupport(author = "zhongtaibao@geoai.com", order = 1)
    @ApiOperation(value = "拍照", notes = "拍照")
    @Trace
    @PostMapping("/start/photograph")
    public Result<Boolean> startPhotograph(@RequestBody @Valid  UavControlVO vo) {
        this.uosUavCodeService.startPhotograph(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @GimbalAutoFollow
    @NestCodeRecord(Constant.EACC_RC_VIRTUAL_STICK_OPEN)
    @ApiOperationSupport(author = "zhongtaibao@geoai.com", order = 1)
    @ApiOperation(value = "打开虚拟摇杆", notes = "打开虚拟摇杆")
    @PostMapping("/batch/open/virtual/stick")
    public Result<Boolean> batchOpenVirtualStick(@RequestBody @Valid  List<UavControlVO> voList) {
        this.uosUavCodeService.batchOpenVirtualStick(voList.stream().map(UosUavCodeTransformer.INSTANCES::transform).collect(Collectors.toList()));
        return Result.ok(true);
    }

    @GimbalAutoFollow
    @NestCodeRecord(Constant.EACC_RC_VIRTUAL_STICK_CONTROL_V2)
    @ApiOperationSupport(author = "zhongtaibao@geoai.com", order = 1)
    @ApiOperation(value = "虚拟摇杆控制", notes = "打开虚拟控制")
    @PostMapping("/v2/batch/control/virtual/stick")
    public Result<Boolean> batchControlVirtualStickV2(@RequestBody @Valid  UavVirtualControlVO vo) {
        this.uosUavCodeService.batchControlVirtualStickV2(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @ApiOperationSupport(author = "zhongtaibao@geoai.com", order = 1)
    @ApiOperation(value = "获取是否支持红外调色板功能", notes = "获取是否支持红外调色板功能")
    @GetMapping("/get/camera/param")
    public Result<CameraParamsRespVO> getCameraParam(@Valid UavCameraParamControlVO vo) {
        CameraParamsOutDTO cameraParam = this.uosUavCodeService.getCameraParam(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(UosUavCodeTransformer.INSTANCES.transform(cameraParam));
    }

    @ApiOperationSupport(author = "zhongtaibao@geoai.com", order = 1)
    @ApiOperation(value = "设置颜色", notes = "设置颜色")
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C54)
    @Trace
    @PostMapping("/set/camera/infraredColor")
    public Result<Boolean> setCameraInfraredColor(@RequestBody @Valid SetCameraInfraredColorReqVO vo) {
        this.uosUavCodeService.setCameraInfraredColor(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok();
    }

    @ApiOperationSupport(author = "zhongtaibao@geoai.com", order = 1)
    @ApiOperation(value = "框选测温和点测温", notes = "框选测温和点测温")
    @NestCodeRecord(Constant.AIRCRAFT_CAMERA_MANAGER_C56)
    @Trace
    @PostMapping("/infrared/area/point/test/temp")
    public Result<BigDecimal> infraredAreaOrPointTestTemperature(@RequestBody @Valid InfraredTestTempeReqVO vo) {
        BigDecimal bigDecimal = this.uosUavCodeService.infraredAreaOrPointTestTemperature(UosUavCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(bigDecimal);
    }
}
