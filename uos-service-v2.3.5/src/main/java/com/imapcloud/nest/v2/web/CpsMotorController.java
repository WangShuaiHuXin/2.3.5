package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.v2.manager.cps.MotorManager;
import com.imapcloud.nest.v2.service.CpsMotorService;
import com.imapcloud.nest.v2.web.vo.req.CpsMotorReqVO;
import com.imapcloud.sdk.pojo.constant.Constant;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * cps指令操作
 *
 * @author boluo
 * @date 2023-03-27
 */
@RequestMapping("v2/cps/motor/")
@RestController
public class CpsMotorController {

    @Resource
    private CpsMotorService cpsMotorService;

    @Resource
    private MotorManager motorManager;

    /**
     * 降落引导
     */
    @NestCodeRecord(Constant.EACC_MOTOR_BOOT_CLOSE)
    @PostMapping("aircraft/landingGuidance/down")
    public Result<Object> landingGuidanceDown(@RequestBody @Valid CpsMotorReqVO cpsMotorReqVO) {

        motorManager.landingGuidanceDown(cpsMotorReqVO.getNestId(), cpsMotorReqVO.getWhich());
        return Result.ok();
    }

//    /**
//     * 无人机开机
//     */
//    @PostMapping("aircraft/on/{nestId}")
//    public Result<Object> aircraftOn(@PathVariable("nestId") String nestId) {
//
//        motorManager.aircraftOn(nestId);
//        return Result.ok();
//    }
//
//    /**
//     * 无人机关机
//     */
//    @PostMapping("aircraft/off/{nestId}")
//    public Result<Object> aircraftOff(@PathVariable("nestId") String nestId) {
//
//        motorManager.aircraftOff(nestId);
//        return Result.ok();
//    }
}
