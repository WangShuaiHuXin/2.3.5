package com.imapcloud.nest.controller;


import com.alibaba.fastjson.JSON;
import com.imapcloud.nest.common.annotation.Trace;
import com.imapcloud.nest.model.NestSensorAudioEntity;
import com.imapcloud.nest.service.NestSensorAudioService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.TraceUuidUtil;
import com.imapcloud.nest.v2.service.BaseNestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/**
 * <p>
 * 机巢喊话器的音频表 前端控制器
 * </p>
 *
 * @author zheng
 * @since 2021-04-06
 */
@Slf4j
@RestController
@RequestMapping("/nestSensorAudio")
public class NestSensorAudioController {

    @Autowired
    private NestSensorAudioService nestSensorAudioService;

    @Autowired
    private BaseNestService baseNestService;

    @GetMapping(value = "/list/{page}/{limit}")
    public RestRes selectListPage(String nestId, String audioName,
                                  @PathVariable("page") Integer pageNum,
                                  @PathVariable("limit") Integer pageSize) {
        return nestSensorAudioService.selectListPage(nestId, audioName, pageNum, pageSize);
    }

    @GetMapping(value = "/all/list")
    public RestRes selectAllListById(String nestId) {
        return nestSensorAudioService.selectAllListById(nestId);
    }

    @PostMapping("/add/to/server")
    public RestRes addAudioToServer(MultipartFile file, NestSensorAudioEntity fileInfoEntity) {
        return nestSensorAudioService.addAudioToServer(file, fileInfoEntity);
    }

    @Trace
    @GetMapping("/add/to/cps")
    public RestRes addAudioToCps(Integer audioId) {
        NestSensorAudioEntity audioEntity = nestSensorAudioService.getById(audioId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", audioEntity != null ? baseNestService.getNestUuidByNestIdInCache(audioEntity.getBaseNestId()) : null, TraceUuidUtil.getTraceUuid(), JSON.toJSONString(audioId));
        RestRes restRes = nestSensorAudioService.addAudioToCps(audioId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", audioEntity != null ? baseNestService.getNestUuidByNestIdInCache(audioEntity.getBaseNestId()) : null, TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @PostMapping("/update")
    public RestRes updateAudio(@RequestBody NestSensorAudioEntity fileInfoEntity) {
        return nestSensorAudioService.updateAudio(fileInfoEntity);
    }

    @PostMapping("/delete")
    public RestRes deleteAudio(@RequestBody Integer[] ids) {
        return nestSensorAudioService.deleteAudio(Arrays.asList(ids));
    }

    @Trace
    @GetMapping("/stop/upload/audio/{nestId}")
    public RestRes stopUploadAudio(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestSensorAudioService.stopUploadAudio(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/reset/speaker/{nestId}")
    public RestRes resetSpeaker(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestSensorAudioService.resetSpeaker(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/start/record/{nestId}")
    public RestRes startRecord(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestSensorAudioService.startRecord(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    @Trace
    @GetMapping("/end/record/{nestId}")
    public RestRes endRecord(@PathVariable String nestId, Long fileSize) {
        Object[] objects = new Object[]{nestId, fileSize};
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(objects));
        RestRes restRes = nestSensorAudioService.endRecord(nestId, fileSize);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @PostMapping("/record/file")
    public RestRes recordAudio(MultipartFile file, String nestId) {
        return nestSensorAudioService.recordAudio(file, nestId);
    }

    @PostMapping("/record")
    public RestRes recordAudio(byte[] bytes, String nestId) {
        return nestSensorAudioService.recordAudio(bytes, nestId);
    }


}

