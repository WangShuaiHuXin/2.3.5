package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.common.annotation.Trace;
import com.imapcloud.nest.v2.service.UosNestCodeService;
import com.imapcloud.nest.v2.service.dto.out.UosNestCodeOutDTO;
import com.imapcloud.nest.v2.web.transformer.UosNestCodeTransformer;
import com.imapcloud.nest.v2.web.vo.req.*;
import com.imapcloud.nest.v2.web.vo.resp.NestNetworkStateVO;
import com.imapcloud.sdk.manager.system.SystemManagerCode;
import com.imapcloud.sdk.pojo.constant.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 控制基站指令
 */

@ApiSupport(author = "wangmin@geoai.com", order = 3)
@Api(value = "基站控制指令API（新）", tags = "基站控制指令API（新）")
@RequestMapping("v2/nest/code")
@RestController
public class UosNestCodeController {

    @Resource
    private UosNestCodeService uosNestCodeService;


    @NestCodeRecord(Constant.EACC_MOTOR_UNITY_OPEN)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "一键开启流程", notes = "流程控制")
    @PostMapping("/one/key/open")
    public Result<Boolean> oneKeyOpen(@RequestBody @Valid NestProcessControlVO vo) {
        this.uosNestCodeService.oneKeyOpen(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }


    @NestCodeRecord(Constant.EACC_MOTOR_UNITY_RECOVERY)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "一键回收流程", notes = "流程控制")
    @PostMapping("/one/key/recycle")
    public Result<Boolean> oneKeyRecycle(@RequestBody @Valid  NestProcessControlVO vo) {
        this.uosNestCodeService.oneKeyRecycle(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_MOTOR_UNITY_RESET)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "一键重置流程", notes = "流程控制")
    @PostMapping("/one/key/reset")
    public Result<Boolean> oneKeyReset(@RequestBody @Valid  NestProcessControlVO vo) {
        this.uosNestCodeService.oneKeyReset(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_MOTOR_BATTERY_LOAD)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "电池卸载流程", notes = "流程控制")
    @PostMapping("/battery/load")
    public Result<Boolean> batteryLoad(@RequestBody @Valid  NestProcessControlVO vo) {
        this.uosNestCodeService.batteryLoad(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_MOTOR_BATTERY_UNLOAD)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "电池装载流程", notes = "流程控制")
    @PostMapping("/battery/unload")
    public Result<Boolean> batteryUnload(@RequestBody @Valid  NestProcessControlVO vo) {
        this.uosNestCodeService.batteryUnload(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_MOTOR_BATTERY_UNLOAD)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "电池热替换流程（G900独有）", notes = "流程控制")
    @PostMapping("/g900/exchange/Battery")
    public Result<Boolean> g900exchangeBattery(@RequestBody @Valid  NestProcessControlVO vo) {
        this.uosNestCodeService.g900exchangeBattery(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    /**
     * 终止任务启动流程
     *
     * @param vo
     * @return
     */
    @NestCodeRecord(Constant.MISSION_MANAGER_C15)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "终止任务启动流程", notes = "流程控制")
    @PostMapping("/stop/start/up/process")
    public Result<Boolean> stopStartUpProcess(@RequestBody @Valid  NestProcessControlVO vo) {
        this.uosNestCodeService.stopStartUpProcess(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }


    @NestCodeRecord(Constant.MISSION_MANAGER_C16)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "终止任务回收流程", notes = "流程控制")
    @PostMapping("/stop/finish/process")
    public Result<Boolean> stopFinishProcess(@RequestBody @Valid  NestProcessControlVO vo) {
        this.uosNestCodeService.stopFinishProcess(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }


    @NestCodeRecord(Constant.EACC_MOTOR_CABIN_OPEN)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "打开舱门", notes = "常规控制")
    @PostMapping("/open/cabin")
    public Result<Boolean> openCabin(@RequestBody @Valid  NestRoutineControlVO vo) {
        this.uosNestCodeService.openCabin(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }


    @NestCodeRecord(Constant.EACC_MOTOR_CABIN_CLOSE)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "关闭舱门", notes = "常规控制")
    @PostMapping("/close/cabin")
    public Result<Boolean> closeCabin(@RequestBody @Valid  NestRoutineControlVO vo) {
        this.uosNestCodeService.closeCabin(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }


    @NestCodeRecord(Constant.EACC_MOTOR_LIFT_RISE)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "升起平台", notes = "常规控制")
    @PostMapping("/rise/lift")
    public Result<Boolean> riseLift(@RequestBody @Valid  NestRoutineControlVO vo) {
        this.uosNestCodeService.riseLift(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_MOTOR_LIFT_DROP)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "降落平台", notes = "常规控制")
    @PostMapping("/down/lift")
    public Result<Boolean> downLift(@RequestBody @Valid  NestRoutineControlVO vo) {
        this.uosNestCodeService.downLift(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_MOTOR_LIFT_CONTROL)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "旋转平台（G503独有）", notes = "常规控制")
    @PostMapping("/rotate/lift")
    public Result<Boolean> rotateLift(@RequestBody @Valid  NestRotateLiftVO vo) {
        this.uosNestCodeService.rotateLift(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_MOTOR_SQUARE_TIGHT)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "归中收紧", notes = "常规控制")
    @PostMapping("/tight/square")
    public Result<Boolean> tightSquare(@RequestBody @Valid  NestSquareControlVO vo) {
        vo.setSquare(2);
        this.uosNestCodeService.tightSquare(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_MOTOR_SQUARE_LOOSE)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "归中释放", notes = "常规控制")
    @PostMapping("/loose/square")
    public Result<Boolean> looseSquare(@RequestBody @Valid  NestSquareControlVO vo) {
        vo.setSquare(2);
        this.uosNestCodeService.looseSquare(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_MOTOR_SQUARE_X_LOOSE)
    @ApiOperationSupport(author = "zhongtaibao@geoai.com")
    @ApiOperation(value = "归中释放x", notes = "常规控制x")
    @Trace
    @PostMapping("/loose/x/square")
    public Result<Boolean> looseXSquare(@RequestBody @Valid  NestSquareControlVO vo) {
        vo.setSquare(0);
        this.uosNestCodeService.looseSquare(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_MOTOR_SQUARE_X_TIGHT)
    @ApiOperationSupport(author = "zhongtaibao@geoai.com")
    @ApiOperation(value = "归中收紧x", notes = "常规控制x")
    @Trace
    @PostMapping("/tight/x/square")
    public Result<Boolean> tightXSquare(@RequestBody @Valid  NestSquareControlVO vo) {
        vo.setSquare(0);
        this.uosNestCodeService.tightSquare(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_MOTOR_SQUARE_Y_LOOSE)
    @ApiOperationSupport(author = "zhongtaibao@geoai.com")
    @ApiOperation(value = "归中释放y", notes = "常规控制y")
    @Trace
    @PostMapping("/loose/y/square")
    public Result<Boolean> looseYSquare(@RequestBody @Valid  NestSquareControlVO vo) {
        vo.setSquare(1);
        this.uosNestCodeService.looseSquare(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_MOTOR_SQUARE_Y_TIGHT)
    @ApiOperationSupport(author = "zhongtaibao@geoai.com")
    @ApiOperation(value = "归中收紧y", notes = "常规控制y")
    @Trace
    @PostMapping("/tight/y/square")
    public Result<Boolean> tightYSquare(@RequestBody @Valid  NestSquareControlVO vo) {
        vo.setSquare(1);
        this.uosNestCodeService.tightSquare(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.TEMPERATURE_MANAGER_C1)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "打开空调", notes = "常规控制")
    @PostMapping("/on/air/conditioner")
    public Result<Boolean> onAirConditioner(@RequestBody @Valid  NestRoutineControlVO vo) {
        this.uosNestCodeService.onAirConditioner(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.TEMPERATURE_MANAGER_C2)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "关闭空调", notes = "常规控制")
    @PostMapping("/off/air/conditioner")
    public Result<Boolean> offAirConditioner(@RequestBody @Valid  NestRoutineControlVO vo) {
        this.uosNestCodeService.offAirConditioner(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C70)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "检测基站网络状态", notes = "系统控制")
    @PostMapping("/detection/network/state")
    public Result<NestNetworkStateVO> detectionNetworkState(@RequestBody @Valid  NestDetectionNetworkVO vo) {
        NestNetworkStateVO nestNetworkStateVO = new NestNetworkStateVO();
        UosNestCodeOutDTO.NestNetworkStateOutDTO nestNetworkStateOutDTO = this.uosNestCodeService.detectionNetworkState(UosNestCodeTransformer.INSTANCES.transform(vo));
        nestNetworkStateVO = UosNestCodeTransformer.INSTANCES.transform(nestNetworkStateOutDTO);
        return Result.ok(nestNetworkStateVO);
    }

    @NestCodeRecord(Constant.MISSION_MANAGER_C27)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "开关备降点", notes = "系统控制")
    @PostMapping("/switch/back/land/point")
    public Result<Boolean> switchBackLandPoint(@RequestBody @Valid  NestBackLandPointVO vo) {
        this.uosNestCodeService.switchBackLandPoint(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_POWER_RESET)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "电源重启", notes = "电源控制")
    @PostMapping("/restart/power")
    public Result<Boolean> restartPower(@RequestBody @Valid  NestPowerControlVO vo) {
        this.uosNestCodeService.restartPower(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_APP_RESET)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "CPS复位", notes = "电源控制")
    @PostMapping("/restart/cps")
    public Result<Boolean> restartCps(@RequestBody @Valid  NestPowerControlVO vo) {
        this.uosNestCodeService.restartCps(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.MPS_RESET)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "MPS复位", notes = "电源控制")
    @PostMapping("/restart/mps")
    public Result<Boolean> restartMps(@RequestBody @Valid  NestPowerControlVO vo) {
        this.uosNestCodeService.restartMps(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.EACC_MOTOR_UNITY_SELF_CHECK)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "系统自检", notes = "电源控制")
    @PostMapping("/system/self/check")
    public Result<Boolean> systemSelfCheck(@RequestBody @Valid  NestPowerControlVO vo) {
        this.uosNestCodeService.systemSelfCheck(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }


    @NestCodeRecord(Constant.MEDIA_MANAGER_2_C204)
    @ApiOperationSupport(author = "wangmin@geoai.com")
    @ApiOperation(value = "基站CPS格式化", notes = "系统控制")
    @PostMapping("/format/cps/memory")
    public Result<Boolean> formatCpsMemory(@RequestBody @Valid  NestSysControlVO vo) {
        this.uosNestCodeService.formatCpsMemory(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(Constant.ANDROID_BOARD_RESET)
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "中控系统重启", notes = "中控系统重启")
    @PostMapping("/android/boards/restart")
    public Result<Boolean> androidBoardsRestart(@RequestBody @Valid  NestProcessControlVO vo) {
        this.uosNestCodeService.androidBoardsRestart(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }


    @NestCodeRecord(Constant.AIRCRAFT_MANAGER_C39)
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "重置推流", notes = "重置推流")
    @PostMapping("/reset/push/stream")
    public Result<Boolean> resetPushStream(@RequestBody @Valid  NestProcessControlVO vo) {
        this.uosNestCodeService.resetPushStream(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }

    @NestCodeRecord(SystemManagerCode.SYSTEM_MANAGER_C18)
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "USB重连", notes = "USB重连")
    @PostMapping("/reconnect/usb")
    public Result<Boolean> reconnectUsb(@RequestBody @Valid  NestProcessControlVO vo) {
        this.uosNestCodeService.reconnectUsb(UosNestCodeTransformer.INSTANCES.transform(vo));
        return Result.ok(true);
    }
}
