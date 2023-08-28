package com.imapcloud.nest.controller;

import com.alibaba.fastjson.JSON;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.Trace;
import com.imapcloud.nest.model.NestRtkEntity;
import com.imapcloud.nest.service.NestRtkService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.TraceUuidUtil;
import com.imapcloud.nest.v2.service.BaseNestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zheng
 * @since 2021-09-15
 */
@ApiSupport(author = "boluo", order = 1)
@Api(value = "基站RTK", tags = "基站RTK")
@Slf4j
@RestController
@RequestMapping("/rtk")
public class NestRtkController {

    @Autowired
    private NestRtkService nestRtkService;

    @Autowired
    private NestService nestService;

    @Autowired
    private BaseNestService baseNestService;

    @Trace
    @GetMapping("/getRtkIsEnable")
    public RestRes getRtkIsEnable(String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestRtkService.getRtkIsEnable(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/openRtk")
    public RestRes openRtk(String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestRtkService.openRtk(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/closeRtk")
    public RestRes closeRtk(String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestRtkService.closeRtk(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/getRtkType")
    public RestRes getRtkType(String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestRtkService.getRtkType(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/setRtkType")
    public RestRes setRtkType(String nestId, Integer type) {
        Object[] objects = new Object[]{nestId, type};
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(objects));
        RestRes restRes = nestRtkService.setRtkType(nestId, type);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/getRtkInfo")
    public RestRes getRtkInfo(String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestRtkService.getRtkInfo(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @PostMapping("/setRtkInfo")
    public RestRes setRtkInfo(@RequestBody NestRtkEntity nestRtkEntity) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestRtkEntity.getBaseNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestRtkEntity));
        RestRes restRes = nestRtkService.setRtkInfo(nestRtkEntity);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestRtkEntity.getBaseNestId()), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @GetMapping("/setExpireTime")
    public RestRes setExpireTime(String nestId, String expireTime) {
        return nestRtkService.setExpireTime(nestId, LocalDate.parse(expireTime));
    }

    @ApiOperationSupport(author = "boluo", order = 1)
    @ApiOperation(value = "获取基站RTK过期时间", notes = "获取基站RTK过期时间")
    @GetMapping("/getExpireTime")
    public RestRes getExpireRtkList() {
        return nestRtkService.getExpireRtkList();
    }

    @GetMapping("/getNestExpireTime")
    public RestRes getNestExpireRtk(String nestId) {
        return nestRtkService.getNestExpireRtk(nestId);
    }

    @PostMapping("/drtk/power/switch/{nestId}")
    public RestRes drtkPowerSwitch(@PathVariable String nestId) {
        return nestRtkService.drtkPowerSwitch(nestId);
    }

}

