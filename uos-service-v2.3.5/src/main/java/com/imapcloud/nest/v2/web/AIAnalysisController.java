package com.imapcloud.nest.v2.web;

import cn.hutool.core.collection.CollUtil;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.common.annotation.NestCodeRecord;
import com.imapcloud.nest.v2.common.enums.AIAnalysisTaskTypeEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.AIAnalysisService;
import com.imapcloud.nest.v2.service.AIStreamingService;
import com.imapcloud.nest.v2.service.dto.in.AIRecognitionTaskInDTO;
import com.imapcloud.nest.v2.service.dto.out.AIAnalysisRepoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestAiStreamingInfoOutDTO;
import com.imapcloud.nest.v2.web.transformer.AIAnalysisTransformer;
import com.imapcloud.nest.v2.web.transformer.AIStreamingTransformer;
import com.imapcloud.nest.v2.web.vo.req.*;
import com.imapcloud.nest.v2.web.vo.resp.AIRecCategoryRespVO;
import com.imapcloud.nest.v2.web.vo.resp.AIRecFunctionExpiredRespVO;
import com.imapcloud.nest.v2.web.vo.resp.AiStreamingInfoRespVO;
import com.imapcloud.sdk.pojo.constant.Constant;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据分析-AI识别API
 * @author Vastfy
 * @date 2022/10/26 09:41
 * @since 2.1.4
 */
@ApiSupport(author = "wumiao@geoai.com", order = 11)
@Api(value = "数据分析-AI识别API（新）", tags = "数据分析-AI识别API（新）")
@RequestMapping("v2/analysis/ai")
@RestController
public class AIAnalysisController {

    @Resource
    private AIAnalysisService aiAnalysisService;

    @Resource
    private AIStreamingService aiStreamingService;

    @Resource
    private AIAnalysisTransformer aiAnalysisTransformer;

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "获取已授权的AI识别功能列表")
    @GetMapping("grf")
    public Result<List<AIRecCategoryRespVO>> getGrantedRecFunctions(@RequestParam(required = false)String orgCode){
        List<AIAnalysisRepoOutDTO> result = aiAnalysisService.getGrantedRecFunctions(orgCode);
        return Result.ok(aiAnalysisTransformer.transform(result));
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 2)
    @ApiOperation(value = "创建图片数据AI识别任务", notes = "该接口会校验用户的已授权识别功能有效性，如果校验不通过，会返回错误码`10300`，失效原因数据通过msg字段（JSON格式）返回")
    @ApiResponses({
            @ApiResponse(code = 10300, message = "部分功能授权已失效", response = AIRecFunctionExpiredRespVO.class, responseContainer = "List")
    })
    @PostMapping("tasks")
    public Result<String> createAIRecognitionTask(@Validated @RequestBody AIRecognitionTaskReqVO body){
        if (CollUtil.isEmpty(body.getDataPhotoIds())) {
            throw new BizException(MessageUtils.getMessage(
                    MessageEnum.GEOAI_UOS_POWER_PARAM_PHOTO_NOT_EMPTY.getContent()));
        }
        if (AIAnalysisTaskTypeEnum.DIALS.getType() == body.getAiTaskType()) {
            if (body.getDataPhotoIds().size() > 200) {
                throw new BizException(MessageUtils.getMessage(
                        MessageEnum.GEOAI_UOS_POWER_PARAM_PHOTO_MAX_200.getContent()));
            }
        }

        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        AIRecognitionTaskInDTO transform = aiAnalysisTransformer.transform(body);
        transform.setOrgCode(orgCode);
        transform.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
        String result = aiAnalysisService.createManualAIAnalysisTask(transform);
        return Result.ok(result);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 3)
    @ApiOperation(value = "暂停图片数据AI识别任务", notes = "该接口只允许在【识别中】状态下调用")
    @PostMapping("tasks/{aiTaskId}/pause")
    public Result<Void>  pauseAIRecognitionTask(@PathVariable String aiTaskId){
        aiAnalysisService.pauseAIRecognitionTask(aiTaskId);
        return Result.ok();
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 4)
    @ApiOperation(value = "继续图片数据AI识别任务", notes = "该接口只允许在【已暂停】状态下调用")
    @PostMapping("tasks/{aiTaskId}/resume")
    public Result<Void>  resumeAIRecognitionTask(@PathVariable String aiTaskId){
        aiAnalysisService.resumeAIRecognitionTask(aiTaskId);
        return Result.ok();
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 4)
    @ApiOperation(value = "终止图片数据AI识别任务", notes = "该接口只允许在【已暂停】状态下调用")
    @PostMapping("tasks/{aiTaskId}/stop")
    public Result<Void>  stopAIRecognitionTask(@PathVariable String aiTaskId){
        aiAnalysisService.stopAIRecognitionTask(aiTaskId);
        return Result.ok();
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 6)
    @ApiOperation(value = "获取已授权的视频流AI识别功能列表")
    @GetMapping("streaming/grf")
    public Result<List<AIRecCategoryRespVO>> getGrantedStreamingRecFunctions(){
        List<AIAnalysisRepoOutDTO> result = aiStreamingService.getGrantedStreamingRecFunctions();
        return Result.ok(aiAnalysisTransformer.transform(result));
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 7)
    @ApiOperation(value = "查询基站AI视频流拉流信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nestId", value = "基站ID", paramType = "path", required = true),
            @ApiImplicitParam(name = "which", value = "基站平台编号", paramType = "query")
    })
    @GetMapping("nest/{nestId}/streaming")
    public Result<List<AiStreamingInfoRespVO>> getNestAiStreamingInfo(@PathVariable String nestId,
                                                                      @RequestParam(required = false) Integer which){
        List<AiStreamingInfoRespVO> results = aiStreamingService.getAiStreamingInfo(nestId, which)
                .stream()
                .map(AIStreamingTransformer.INSTANCES::transform)
                .collect(Collectors.toList());
        return Result.ok(results);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 8)
    @ApiOperation(value = "开启视频流AI识别")
    @PostMapping("streaming/open")
    @NestCodeRecord("VIDEO_AI_OPEN")
    public Result<AiStreamingInfoRespVO> openAiStreaming(@RequestBody AiStreamingOpenReqVO body){
        NestAiStreamingInfoOutDTO result = aiStreamingService.openAiStreaming(AIStreamingTransformer.INSTANCES.transform(body));
        return Result.ok(AIStreamingTransformer.INSTANCES.transform(result));
    }


    @ApiOperationSupport(author = "wumiao@geoai.com", order = 9)
    @ApiOperation(value = "退出视频流AI识别")
    @PostMapping("streaming/exit")
    @NestCodeRecord("VIDEO_AI_EXIT")
    public Result<Void> exitAiStreaming(@RequestBody AiStreamingExitReqVO body){
        aiStreamingService.exitAiStreaming(AIStreamingTransformer.INSTANCES.transform(body));
        return Result.ok();
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 10)
    @ApiOperation(value = "切换视频流AI识别")
    @PostMapping("streaming/switch")
    @NestCodeRecord("VIDEO_AI_SWITCH")
    public Result<AiStreamingInfoRespVO> switchAiStreaming(@RequestBody AiStreamingSwitchReqVO body){
        NestAiStreamingInfoOutDTO result = aiStreamingService.switchAiStreaming(AIStreamingTransformer.INSTANCES.transform(body));
        return Result.ok(AIStreamingTransformer.INSTANCES.transform(result));
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 11)
    @ApiOperation(value = "视频流AI识别告警设置")
    @PostMapping("streaming/alarm/settings")
    public Result<Void> setAiStreamingAlarmSettings(@RequestBody AiStreamingAlarmSettingReqVO body){
        aiStreamingService.setAiStreamingAlarmSettings(AIStreamingTransformer.INSTANCES.transform(body));
        return Result.ok();
    }

}
