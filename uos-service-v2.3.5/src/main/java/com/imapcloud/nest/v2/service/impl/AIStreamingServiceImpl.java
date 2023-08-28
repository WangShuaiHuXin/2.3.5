package com.imapcloud.nest.v2.service.impl;

import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.JsonUtils;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.geoai.rocketmq.spring.core.RocketMQTemplate;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.enums.RecDataTypeEnum;
import com.imapcloud.nest.v2.common.exception.AIRecFunctionExpiredException;
import com.imapcloud.nest.v2.common.exception.AIRecFunctionUnauthorizedException;
import com.imapcloud.nest.v2.common.exception.VideoAIChannelExceedException;
import com.imapcloud.nest.v2.common.exception.VideoAIServiceException;
import com.imapcloud.nest.v2.common.properties.AnalysisConfig;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.in.AIStreamingExitInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.AIStreamingOpenInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.OrgVideoRecFunQueryInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.UdaAlTaskResponseOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.UdaAlgorithmRepoOutDO;
import com.imapcloud.nest.v2.manager.feign.UdaAnalysisServiceClient;
import com.imapcloud.nest.v2.manager.rest.MediaManager;
import com.imapcloud.nest.v2.service.AIStreamingService;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.service.dto.out.AIAnalysisRepoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseUavInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestAiStreamingInfoOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.LivePlayInfoRespVO;
import com.imapcloud.nest.v2.web.vo.resp.PushStreamInfoRespVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.apis.message.MessageId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * AI视频流业务接口实现
 *
 * @author Vastfy
 * @date 2022/12/24 16:38
 * @since 2.1.7
 */
@Slf4j
@Service
public class AIStreamingServiceImpl implements AIStreamingService {

    private static final String STREAMING_CACHE_KEY_PREFIX = "geoai:uos:streaming:";
//    private static final String STREAMING_APP_AI = "ai";
    private static final String STREAMING_APP_AI = "live";

    private static final String HTTPS_PROTOCOL = "https";
    private static final String HTTP_PROTOCOL = "http";
    private static final String RTMP_PROTOCOL = "rtmp";
    private static final String FLV_FORMAT = ".flv";

    @Resource
    private UdaAnalysisServiceClient udaAnalysisServiceClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

//    @Resource
//    private MediaStreamService mediaStreamService;

    @Resource
    private BaseUavService baseUavService;

//    @Resource
//    private BaseNestService baseNestService;

    @Resource
    private MediaManager mediaManager;

    @Override
    public List<AIAnalysisRepoOutDTO> getGrantedStreamingRecFunctions() {
        // 查询支持视频的已授权识别功能
        Result<List<UdaAlgorithmRepoOutDO>> result;
        try {
            result = udaAnalysisServiceClient.getUdaGrantedFunctions(RecDataTypeEnum.STREAMING.getType());
        }catch (Exception e){
            log.error("获取视频流识别功能失败", e);
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_OBTAIN_VIDEO_REC_FUNCTION_FAILED.getContent()));
        }
        if(!result.isOk()){
            return Collections.emptyList();
        }
        List<UdaAlgorithmRepoOutDO> data = result.getData();
        if(CollectionUtils.isEmpty(data)){
            return Collections.emptyList();
        }
        return data.stream()
                .map(r -> {
                    AIAnalysisRepoOutDTO info = new AIAnalysisRepoOutDTO();
                    info.setCategoryId(r.getStorageId());
                    info.setCategoryName(r.getName());
                    if(!CollectionUtils.isEmpty(r.getDiscernFunctionInfos())){
                        List<AIAnalysisRepoOutDTO.AIRecFunction> aiRecFunctions = r.getDiscernFunctionInfos()
                                .stream()
                                .map(e -> {
                                    AIAnalysisRepoOutDTO.AIRecFunction fun = new AIAnalysisRepoOutDTO.AIRecFunction();
                                    fun.setFunctionId(e.getFunctionId());
                                    fun.setFunctionName(e.getName());
                                    fun.setVersion(e.getVersion());
                                    return fun;
                                })
                                .collect(Collectors.toList());
                        info.setRecFunctions(aiRecFunctions);
                    }
                    return info;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<NestAiStreamingInfoOutDTO> getAiStreamingInfo(String nestId, Integer which) {
        // 根据基站ID和平台编号，查询Redis缓存
        // redis key ==> geoai:uos:streaming:{nest}:{which|*}
        if(!StringUtils.hasText(nestId)){
            return Collections.emptyList();
        }
        String who = Objects.isNull(which) ? "*" : which.toString();
        String cacheKey = STREAMING_CACHE_KEY_PREFIX + nestId + SymbolConstants.COLON + who;
        Set<String> keys;
        if(Objects.isNull(which)){
            keys = stringRedisTemplate.keys(cacheKey);
        }else{
            keys = Collections.singleton(cacheKey);
        }
        if(CollectionUtils.isEmpty(keys)){
            return Collections.emptyList();
        }
        return keys.stream()
                .map(ck -> {
                    String cacheValue = stringRedisTemplate.opsForValue().get(ck);
                    if (!StringUtils.hasText(cacheValue)) {
                        return null;
                    }
                    return JsonUtils.readJson(cacheValue, NestAiStreamingInfoOutDTO.class).orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public NestAiStreamingInfoOutDTO openAiStreaming(AiStreamingOpenInDTO data) {
        String nestId = data.getNestId();
        Integer which = data.getWhich();
        String functionId = data.getFunctionId();
        String missionRecordId = data.getMissionRecordId();
        // 校验【基站ID+平台编号】是否已开启视频流识别
        List<NestAiStreamingInfoOutDTO> aiStreamingInfos = getAiStreamingInfo(nestId, which);
        // 已开启
        if(!CollectionUtils.isEmpty(aiStreamingInfos)){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HAS_ENABLED_VIDEO_AI_RECOGNITION.getContent()));
        }
        String streamName = nestId + (Objects.nonNull(which) ? SymbolConstants.UNDER_LINE + which : "");
        synchronized (streamName.intern()){
            aiStreamingInfos = getAiStreamingInfo(nestId, which);
            if(!CollectionUtils.isEmpty(aiStreamingInfos)){
                throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HAS_ENABLED_VIDEO_AI_RECOGNITION.getContent()));
            }
//            @Deprecated 2.3.2
//            Map<String, String> params = Collections.singletonMap("mrId", missionRecordId);
//            // 生成AI推流地址[算法使用] ==> rtmp://push.live.geoai.com:1935/ai/9527666_1?missionRecordId=666
//            String pushRtmp = NmsStreamUtils.getPushRtmp(STREAMING_APP_AI, streamName, params);
//            // 获取基站无人机图传推流地址（rtmp协议）
//            String streamPullUrl = findNestUavPullStreamUrl(nestId, which);
//            // 生成AI拉流地址[前端使用] ==> http://live.geoai.com/ai/9527666_1.flv?missionRecordId=666
//            String pullHttp = NmsStreamUtils.getPullHttp(STREAMING_APP_AI, streamName, params);
            // 获取基站无人机图传推流地址（rtmp协议）[算法使用]
            String streamPullUrl = findNestUavPullStreamUrl(nestId, which);
            PushStreamInfoRespVO aiStreamInfo = mediaManager.getOrCreateAiStreamInfo(STREAMING_APP_AI, streamName);
            // 生成AI推流地址[算法使用] ==> rtmp://push.live.geoai.com:1935/live/9527666_1?mrId=666
            String pushRtmp = aiStreamInfo.getPushUrl() + "&mrId=" + missionRecordId;
            // 生成AI拉流地址[前端使用] ==> http://live.geoai.com/live/9527666_1.flv?mrId=666
            String pullHttp = aiStreamInfo.getPullUrl();
            // 调用UDA接口
            Result<UdaAlTaskResponseOutDO> result;
            try {
                AIStreamingOpenInDO body = new AIStreamingOpenInDO();
                body.setFunctionId(functionId);
                body.setAiStreamPullUrl(streamPullUrl);
                body.setAiStreamPushUrl(pushRtmp);
                result = udaAnalysisServiceClient.openAiStreaming(body);
            }catch (Exception e){
                log.error("开启视频流AI识别失败", e);
                throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_OPEN_VIDEO_AI_RECOGNITION_ERROR.getContent()));
            }
            if(!result.isOk()){
                log.error("开启视频流AI识别失败 ==> {}", result);
                if(Objects.equals(result.getCode(), "10600")){
                    if(log.isDebugEnabled()){
                        log.debug("UDA部分算法授权已失效，原因 ==> {}", result.getData().getAuthErrorList());
                    }
                    throw new AIRecFunctionExpiredException(JsonUtils.writeJson(result.getData().getAuthErrorList()));
                }
                if(Objects.equals(result.getCode(), "10601")){
                    throw new AIRecFunctionUnauthorizedException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_VIDEO_AI_REC_FUNCTION_UNAUTHORIZED.getContent()));
                }
                if(Objects.equals(result.getCode(), "10610")){
                    throw new VideoAIChannelExceedException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_VIDEO_AI_SYS_CHANNEL_EXCEED.getContent()));
                }
                if(Objects.equals(result.getCode(), "10611")){
                    throw new VideoAIChannelExceedException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_VIDEO_AI_ORG_CHANNEL_EXCEED.getContent()));
                }
                if(Objects.equals(result.getCode(), "10612")){
                    throw new VideoAIServiceException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_VIDEO_AI_SERVICE_ERROR.getContent()));
                }
                throw new VideoAIServiceException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_OPEN_VIDEO_AI_RECOGNITION_FAILED.getContent()));
            }
            log.info("开启识别AI任务成功，AI任务ID ==> [{}]", result.getData());
            // 缓存值redis
            NestAiStreamingInfoOutDTO cache = new NestAiStreamingInfoOutDTO();
            cache.setNestId(nestId);
            cache.setWhich(which);
            cache.setFunctionId(functionId);
            cache.setMissionRecordId(missionRecordId);
            cache.setEnableAlarm(data.isEnableAlarm());
            cache.setAiStreamPullUrl(pullHttp);
            cache.setAiStreamPushUrl(pushRtmp);
            cache.setProcessId(result.getData().getTaskId());
            cache.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
            cache.setUsername(TrustedAccessTracerHolder.get().getUsername());
            cache.setOrgCode(TrustedAccessTracerHolder.get().getOrgCode());
            AnalysisConfig analysisConfig = geoaiUosProperties.getAnalysis();
            long aiStreamTtl = analysisConfig.getAiStreamTtl();
            String cacheKey = STREAMING_CACHE_KEY_PREFIX + nestId + SymbolConstants.COLON + which;
            String json = JsonUtils.writeJson(cache);
            stringRedisTemplate.opsForValue().set(cacheKey, json, aiStreamTtl, TimeUnit.MILLISECONDS);
            // 发送超时消息，防止异常情况下UDA原进程仍存在问题
            long ttl = System.currentTimeMillis() + aiStreamTtl;
            MessageId messageId = rocketMQTemplate.sendAndReceive(analysisConfig.getAiStreamTtlTopic(), cache, ttl);
            log.info("发送AI视频流超时检查任务成功, 消息ID ==> [{}], 超时检查时间 ==> [{}]", messageId, ttl);
            return cache;
        }
    }

    private String findNestUavPullStreamUrl(String nestId, Integer which) {
        // 1. 根据基站查询无人机列表
        List<BaseUavInfoOutDTO> uavInfos = baseUavService.listUavInfosByNestIds(Collections.singletonList(nestId));
        if(CollectionUtils.isEmpty(uavInfos)){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_UAV_INFORMATION_IS_FOUND.getContent()));
        }
        Optional<BaseUavInfoOutDTO> first = uavInfos.stream()
                .filter(r -> Objects.equals(r.getWhich(), which))
                .findFirst();
        if(!first.isPresent()){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UNMATCHED_UAV_INFORMATION.getContent()));
        }
        // 2. 根据无人机ID和which查询拉流地址
//        @Deprecated 2.3.2
//        String streamPullUrl = mediaStreamService.getStreamPullUrl(first.get().getStreamId());
//        if(!StringUtils.hasText(streamPullUrl)){
//            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_CONFIGURED_WITH_UAV_STREAMING_INFO.getContent()));
//        }
//        return streamPullUrl.replace(HTTPS_PROTOCOL, RTMP_PROTOCOL)
//                .replace(HTTP_PROTOCOL, RTMP_PROTOCOL)
//                .replace(FLV_FORMAT, SymbolConstants.EMPTY);
        String uavId = first.get().getUavId();
        LivePlayInfoRespVO uavLive = baseUavService.playUavLive(uavId, false);
        return uavLive.getRtmp();
    }

    @Override
    public NestAiStreamingInfoOutDTO exitAiStreaming(AiStreamingExitInDTO data) {
        // 校验【基站ID+平台编号】是否已开启视频流识别
        List<NestAiStreamingInfoOutDTO> aiStreamingInfos = getAiStreamingInfo(data.getNestId(), data.getWhich());
        // 已开启
        if(CollectionUtils.isEmpty(aiStreamingInfos)){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_ENABLED_VIDEO_AI_RECOGNITION.getContent()));
        }
        NestAiStreamingInfoOutDTO cache = aiStreamingInfos.get(0);

        // 校验用户单位识别功能授权过期列表
        checkVideoRecognitionFunctionValid(cache.getFunctionId());

        // 调用UDA退出AI识别接口
        Result<Void> result;
        try {
            AIStreamingExitInDO body = new AIStreamingExitInDO();
            body.setOrgCode(cache.getOrgCode());
            body.setProcessId(cache.getProcessId());
            result = udaAnalysisServiceClient.exitAiStreaming(body);
        }catch (Exception e){
            log.error("退出视频流AI识别异常", e);
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXIT_VIDEO_AI_RECOGNITION_ERROR.getContent()));
        }
        if(!result.isOk()){
            log.error("退出视频流AI识别失败 ==> {}", result);
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXIT_VIDEO_AI_RECOGNITION_FAILED.getContent()));
        }
        // 清理Redis缓存
        String cacheKey = STREAMING_CACHE_KEY_PREFIX + data.getNestId() + SymbolConstants.COLON + data.getWhich();
        Boolean delete = stringRedisTemplate.delete(cacheKey);
        if(Boolean.TRUE.equals(delete)){
            log.info("清理视频AI缓存信息成功 ==> {}", cache);
        }

        return cache;
    }

    @Override
    public void terminateAiStreaming(String nestId, Integer which) {
        List<NestAiStreamingInfoOutDTO> aiStreamingInfos = getAiStreamingInfo(nestId, which);
        if(CollectionUtils.isEmpty(aiStreamingInfos)){
            log.info("基站[nestId={},which={}]未开启AI识别，无需退出任务", nestId, which);
            return;
        }
        NestAiStreamingInfoOutDTO cache = aiStreamingInfos.get(0);

        try {
            AIStreamingExitInDO body = new AIStreamingExitInDO();
            body.setOrgCode(cache.getOrgCode());
            body.setProcessId(cache.getProcessId());
            Result<Void> result = udaAnalysisServiceClient.exitAiStreaming(body);
            if(!result.isOk()){
                log.error("退出视频流AI识别失败 ==> {}", result);
                return;
            }
            log.info("退出视频AI识别成功");
        }catch (Exception e){
            log.error("退出视频流AI识别失败", e);
        }

        // 清理Redis缓存
        try {
            String cacheKey = STREAMING_CACHE_KEY_PREFIX + nestId + SymbolConstants.COLON + which;
            Boolean delete = stringRedisTemplate.delete(cacheKey);
            if(Boolean.TRUE.equals(delete)){
                log.info("清理视频AI缓存信息成功 ==> {}", cache);
            }
        }catch (Exception e){
            log.error("清理视频AI缓存信息失败", e);
        }
    }

    @Override
    public NestAiStreamingInfoOutDTO switchAiStreaming(AiStreamingSwitchInDTO data) {

        // 退出原识别功能
        AiStreamingExitInDTO exit = new AiStreamingExitInDTO();
        exit.setNestId(data.getNestId());
        exit.setWhich(data.getWhich());
        NestAiStreamingInfoOutDTO cache = exitAiStreaming(exit);

        // 开启新识别功能
        AiStreamingOpenInDTO open = new AiStreamingOpenInDTO();
        open.setNestId(data.getNestId());
        open.setWhich(data.getWhich());
        open.setEnableAlarm(data.isEnableAlarm());
        open.setFunctionId(data.getFunctionId());
        open.setMissionRecordId(cache.getMissionRecordId());
        return openAiStreaming(open);
    }

    @Override
    public void timeoutTerminateAiStreaming(AiStreamingTerminateInDTO data) {
        try {
            AIStreamingExitInDO body = new AIStreamingExitInDO();
            body.setOrgCode(data.getOrgCode());
            body.setProcessId(data.getProcessTaskId());
            Result<Void> result = udaAnalysisServiceClient.exitAiStreaming(body);
            if(!result.isOk()){
                log.error("终止视频流AI识别失败 ==> {}", result);
                return;
            }
            log.info("终止视频流AI识别任务[{}]成功", data.getProcessTaskId());
        }catch (Exception e){
            log.error("终止视频流AI识别失败", e);
        }
        // 超时删除识别任务缓存
        List<NestAiStreamingInfoOutDTO> aiStreamingInfos = getAiStreamingInfo(data.getNestId(), data.getWhich());
        if(!CollectionUtils.isEmpty(aiStreamingInfos)){
            NestAiStreamingInfoOutDTO cache = aiStreamingInfos.get(0);
            // 任务相同时才清理
            if(Objects.equals(cache.getProcessId(), data.getProcessTaskId())){
                // 清理Redis缓存
                String cacheKey = STREAMING_CACHE_KEY_PREFIX + data.getNestId() + SymbolConstants.COLON + data.getWhich();
                Boolean delete = stringRedisTemplate.delete(cacheKey);
                if(Boolean.TRUE.equals(delete)){
                    log.info("清理视频AI缓存信息成功 ==> {}", cache);
                }
            }
        }
    }

    @Override
    public void setAiStreamingAlarmSettings(AiStreamingAlarmSettingInDTO data) {
        // 校验【基站ID+平台编号】是否已开启视频流识别
        List<NestAiStreamingInfoOutDTO> aiStreamingInfos = getAiStreamingInfo(data.getNestId(), data.getWhich());
        // 已开启
        if(CollectionUtils.isEmpty(aiStreamingInfos)){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_ENABLED_VIDEO_AI_RECOGNITION.getContent()));
        }
        NestAiStreamingInfoOutDTO cache = aiStreamingInfos.get(0);

        cache.setEnableAlarm(data.isEnableAlarm());
        String cacheKey = STREAMING_CACHE_KEY_PREFIX + data.getNestId() + SymbolConstants.COLON + data.getWhich();
        stringRedisTemplate.opsForValue().set(cacheKey, JsonUtils.writeJson(cache));
    }

    private void checkVideoRecognitionFunctionValid(String functionId) {
        Result<List<UdaAlTaskResponseOutDO.AuthError>> result;
        try {
            OrgVideoRecFunQueryInDO params = new OrgVideoRecFunQueryInDO();
            params.setFunctionId(functionId);
            params.setOrgCode(TrustedAccessTracerHolder.get().getOrgCode());
            result = udaAnalysisServiceClient.getOrgVideoAuthExpiredList(params);
        }catch (Exception e){
            log.error("检验视频AI识别功能异常", e);
            throw new VideoAIServiceException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CHECK_VIDEO_AI_RECOGNITION_ERROR.getContent()));
        }

        if(Objects.equals(result.getCode(), "10601")){
            throw new AIRecFunctionUnauthorizedException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_VIDEO_AI_REC_FUNCTION_UNAUTHORIZED.getContent()));
        }
        else if(!result.isOk()){
            log.error("检验视频AI识别功能失败 ==> {}", result);
            throw new VideoAIServiceException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CHECK_VIDEO_AI_RECOGNITION_FAILED.getContent()));
        }
        else if(!CollectionUtils.isEmpty(result.getData())){
            throw new AIRecFunctionExpiredException(JsonUtils.writeJson(result.getData()));
        }
    }

}
