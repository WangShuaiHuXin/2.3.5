package com.imapcloud.nest.controller;


import com.alibaba.fastjson.JSON;
import com.imapcloud.nest.common.annotation.Trace;
import com.imapcloud.nest.service.NestSensorAudioService;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.TraceUuidUtil;
import com.imapcloud.nest.v2.service.BaseNestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 机巢附属系统控制器
 * Created by wmin on 2020/9/16 14:22
 */
@Slf4j
@RestController
@RequestMapping("/accessory")
public class AccessoryController {

    @Autowired
    private NestService nestService;

    @Autowired
    private NestSensorAudioService nestSensorAudioService;

    @Autowired
    private BaseNestService baseNestService;


    /**
     * 获取播放列表
     *
     * @param nestId
     * @return
     */
    @Trace
    @GetMapping("/list/audio/{nestId}")
    public RestRes listAudioFromAircraft(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestSensorAudioService.listAudioFromAircraft(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @GetMapping("/delete/audio")
    public RestRes delete(String nestId, String index) {
        Object[] objects = new Object[]{nestId, index};
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(objects));
        RestRes restRes = nestSensorAudioService.delete(nestId, index);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    /**
     * 设置喊话器音量
     *
     * @param param
     * @return
     */
    @Trace
    @PostMapping("/set/speaker/volume")
    public RestRes setSpeakerVolume(@RequestBody Map<String, String> param) {
        String nestId = param != null ? param.get("nestId") : null;
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(param));
        RestRes restRes = nestSensorAudioService.setSpeakerVolume(param);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @PostMapping("/set/if/repeat/play")
    public RestRes setIfRepeatPlay(@RequestBody Map<String, String> param) {
        String nestId = param != null ? param.get("nestId") : null;
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(param));
        RestRes restRes = nestSensorAudioService.setIfRepeatPlay(param);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    /**
     * 喊话器播放
     *
     * @param param
     * @return
     */
    @Trace
    @PostMapping("/speaker/play/audio")
    public RestRes speakerPlayAudio(@RequestBody Map<String, Object> param) {
        String nestId = param != null ? String.valueOf(param.get("nestId")) : null;
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(param));
        RestRes restRes = nestSensorAudioService.speakerPlayAudio(param);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    /**
     * 停止播放
     *
     * @param nestId
     * @return
     */
    @Trace
    @PostMapping("/speaker/stop/audio/{nestId}")
    public RestRes speakerStopAudio(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestSensorAudioService.speakerStopAudio(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @PostMapping("/audio/rename")
    public RestRes speakerPlayListRename(@RequestBody Map<String, Object> param) {
        String nestId = param != null ? String.valueOf(param.get("nestId")) : null;
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(param));
        RestRes restRes = nestSensorAudioService.speakerPlayListRename(param);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }


    /**
     * 打开夜航灯
     *
     * @param nestId
     * @return
     */
    @Trace
    @PostMapping("/open/beacon/{nestId}")
    public RestRes openBeacon(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestSensorAudioService.openBeacon(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @PostMapping("/close/beacon/{nestId}")
    public RestRes closeBeacon(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestSensorAudioService.closeBeacon(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @PostMapping("/open/spotlight/{nestId}")
    public RestRes openSpotlight(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestSensorAudioService.openSpotlight(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @PostMapping("/close/spotlight/{nestId}")
    public RestRes closeSpotlight(@PathVariable String nestId) {
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestSensorAudioService.closeSpotlight(nestId);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

    @Trace
    @PostMapping("/set/spotlight/brightness")
    public RestRes setSpotlightBrightness(@RequestBody Map<String, String> param) {
        String nestId = param != null ?param.get("nestId") : null;
        log.info("mqttLogTrace?nestUuid={}&nodeId=srv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(nestId));
        RestRes restRes = nestSensorAudioService.setSpotlightBrightness(param);
        log.info("mqttLogTrace?nestUuid={}&nodeId=ssv&traceId={}&body={}", baseNestService.getNestUuidByNestIdInCache(nestId), TraceUuidUtil.getTraceUuid(), JSON.toJSONString(restRes));
        return restRes;
    }

}
