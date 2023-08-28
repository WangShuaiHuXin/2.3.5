package com.imapcloud.nest.v2.web;

import com.geoai.common.web.rest.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.common.annotation.NestUUID;
import com.imapcloud.nest.v2.service.DJILiveService;
import com.imapcloud.nest.v2.service.dto.out.DJICommonResultOutDTO;
import com.imapcloud.nest.v2.web.transformer.DJICommonResultTransformer;
import com.imapcloud.nest.v2.web.vo.req.VideoQualityReqVO;
import com.imapcloud.nest.v2.web.vo.resp.DJICommonResultRespVO;
import com.imapcloud.sdk.pojo.constant.DjiDockTopic;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJILiveController.java
 * @Description DJILiveController
 * @createTime 2022年08月16日 11:57:00
 */
@ApiSupport(author = "zhongtaibao@geoai.com", order = 1)
@Api(value = "", tags = "大疆机场直播")
@RequestMapping("v2/dji/steam")
@RestController
public class DJILiveController {

    @Resource
    private DJILiveService djiLiveService;

    /**
     *  设置视频分辨率
     * @param videoQualityReqVO
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "设置视频分辨率", notes = "设置视频分辨率")
    @NestCodeRecord(DjiDockTopic.LIVE_SET_QUALITY)
    @PostMapping("/video/quality/{type}/{uuid}")
    public Result<Boolean> setVideoQuality(@PathVariable("uuid") @Valid @NotNull(message = "uuid 不能为空") @NestUUID String uuid
            ,@PathVariable("type") @Valid @NotNull(message = "type 不能为空") Integer type
            ,@RequestBody @Valid VideoQualityReqVO videoQualityReqVO){
        this.djiLiveService.setVideoQuality(type , uuid , videoQualityReqVO.getVideoId(), videoQualityReqVO.getVideoQuality());
        return Result.ok();
    }

    /**
     *  开始直播
     * @param uuid
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "开始直播", notes = "开始直播")
    @NestCodeRecord(DjiDockTopic.LIVE_START_PUSH)
    @PostMapping("/live/start/push/{type}/{uuid}")
    public Result<DJICommonResultRespVO.LiveResultRespVO> liveStartPush(@PathVariable("uuid") @Valid @NotNull(message = "uuid 不能为空") @NestUUID String uuid
            ,@PathVariable("type") @Valid @NotNull(message = "type 不能为空") Integer type
            ,@RequestBody @Valid VideoQualityReqVO videoQualityReqVO){
        DJICommonResultOutDTO.LiveResultOutDTO liveResultOutDTO = this.djiLiveService.livePush(uuid , type , Boolean.TRUE ,videoQualityReqVO.getVideoId(), videoQualityReqVO.getVideoQuality());
        DJICommonResultRespVO.LiveResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(liveResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }


    /**
     *  停止直播
     * @param uuid
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "停止直播", notes = "停止直播")
    @NestCodeRecord(DjiDockTopic.LIVE_STOP_PUSH)
    @PostMapping("/live/close/push/{type}/{uuid}")
    public Result<DJICommonResultRespVO.LiveResultRespVO> liveClosePush(@PathVariable("uuid") @Valid @NotNull(message = "uuid 不能为空") @NestUUID String uuid
            ,@PathVariable("type") @Valid @NotNull(message = "type 不能为空") Integer type
            ,@RequestBody @Valid VideoQualityReqVO videoQualityReqVO){
        DJICommonResultOutDTO.LiveResultOutDTO liveResultOutDTO = this.djiLiveService.livePush(uuid,type,Boolean.FALSE , videoQualityReqVO.getVideoId(), videoQualityReqVO.getVideoQuality());
        DJICommonResultRespVO.LiveResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(liveResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }

    /**
     *  刷新直播
     * @param uuid
     * @return
     */
    @ApiOperationSupport(author = "zhongtaibaoo@geoai.com", order = 1)
    @ApiOperation(value = "刷新直播", notes = "刷新直播")
    @NestCodeRecord({DjiDockTopic.LIVE_STOP_PUSH,DjiDockTopic.LIVE_START_PUSH})
    @PostMapping("/live/refresh/push/{type}/{uuid}")
    public Result<DJICommonResultRespVO.LiveResultRespVO> refreshLive(@PathVariable("uuid") @Valid @NotNull(message = "uuid 不能为空") @NestUUID String uuid
            ,@PathVariable("type") @Valid @NotNull(message = "type 不能为空") Integer type
            ,@RequestBody @Valid VideoQualityReqVO videoQualityReqVO){
        DJICommonResultOutDTO.LiveResultOutDTO liveResultOutDTO = this.djiLiveService.flushLivePush(uuid, type , videoQualityReqVO.getVideoId(), videoQualityReqVO.getVideoQuality());
        DJICommonResultRespVO.LiveResultRespVO djiCommonResultRespVO = DJICommonResultTransformer.INSTANCES.transform(liveResultOutDTO);
        return Result.ok(djiCommonResultRespVO);
    }




}
