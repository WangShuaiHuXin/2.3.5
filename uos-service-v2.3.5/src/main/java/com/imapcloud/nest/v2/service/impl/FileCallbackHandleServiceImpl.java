package com.imapcloud.nest.v2.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoai.common.web.rest.CommonErrorCode;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.service.MissionVideoPhotoService;
import com.imapcloud.nest.service.MissionVideoService;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.service.FileCallbackHandleService;
import com.imapcloud.nest.v2.service.dto.in.RecordTaskVideoInDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 文件回调处理业务接口实现
 *
 * @author Vastfy
 * @date 2023/2/27 10:58
 * @since 2.2.3
 */
@Slf4j
@Service
public class FileCallbackHandleServiceImpl implements FileCallbackHandleService {

    private static final String CALLBACK_TYPE_KEY = "type";
    private static final String CALLBACK_DATA_KEY = "data";
    private static final String TYPE_CHUNK_COMPOSED_RESULT = "CHUNK_COMPOSED_RESULT";
    private static final String TYPE_VIDEO_FRAME_EXTRACTION = "VIDEO_FRAME_EXTRACTION";
    private static final String TYPE_VIDEO_RECORDING_COMPLETED = "VIDEO_RECORDING_COMPLETED";

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Resource
    private MissionVideoPhotoService missionVideoPhotoService;

    @Resource
    private MissionVideoService missionVideoService;

    @Override
    public void handleFileCallback(String callbackData) {
        log.info("Listen data ==> {}", callbackData);
        JsonNode jsonTree = null;
        try {
            jsonTree = objectMapper.readTree(callbackData);
        } catch (JsonProcessingException e) {
            log.error("Parse business data error", e);
        }
        if(Objects.isNull(jsonTree)){
            return;
        }
        JsonNode typeNode = jsonTree.get(CALLBACK_TYPE_KEY);
        if(Objects.isNull(typeNode)){
            log.warn("无法解析业务回调类型");
            return;
        }
        String type = typeNode.asText();
        // 文件合并结果处理
        if(Objects.equals(type, TYPE_CHUNK_COMPOSED_RESULT)){
            doHandleChunkComposedResult(jsonTree);
            return;
        }
        // 视频抽帧处理
        if(Objects.equals(type, TYPE_VIDEO_FRAME_EXTRACTION)){
            doHandleVideoFrameExtraction(jsonTree);
        }
        // 录像视频同步处理
        if(Objects.equals(type, TYPE_VIDEO_RECORDING_COMPLETED)){
            doHandleVideoRecordingCompleted(jsonTree);
        }
    }

    private void doHandleChunkComposedResult(JsonNode typeNode) {
        JsonNode dataNode = typeNode.get(CALLBACK_DATA_KEY);
        if(Objects.isNull(dataNode)){
            log.warn("文件合并结果数据解析为空");
            return;
        }
        UosFileComposeResult composeResult = null;
        try {
            composeResult = objectMapper.readValue(dataNode.traverse(), UosFileComposeResult.class);
        } catch (IOException e) {
            log.error("Parse file compose data error", e);
        }
        if(Objects.isNull(composeResult)){
            return;
        }
        String message;
        if(CommonErrorCode.OK.toBizErrorCode().equals(composeResult.getCode())){
            message = WebSocketRes.ok().topic(WebSocketTopicEnum.CHUNK_COMPOSED_RESULT).data("dto", composeResult).toJSONString();
        }
        else{
            message = WebSocketRes.err().topic(WebSocketTopicEnum.CHUNK_COMPOSED_RESULT).data("dto", composeResult).msg(composeResult.getMsg()).toJSONString();
        }
        Optional<String> optional = getAccountName(composeResult.getAccountId());
        // TODO 异步发送
        optional.ifPresent(r -> ChannelService.sendMessageByType13Channel(r, message));
    }

    private void doHandleVideoFrameExtraction(JsonNode typeNode) {
        JsonNode dataNode = typeNode.get(CALLBACK_DATA_KEY);
        if(Objects.isNull(dataNode)){
            log.warn("视频抽帧结果数据解析为空");
            return;
        }
        VideoFrameResult result = null;
        try {
            result = objectMapper.readValue(dataNode.traverse(), VideoFrameResult.class);
        } catch (IOException e) {
            log.error("Parse video frame data error", e);
        }
        if(Objects.isNull(result)){
            return;
        }
        missionVideoPhotoService.handleExtractingResult(result);
    }

    private void doHandleVideoRecordingCompleted(JsonNode typeNode) {
        JsonNode dataNode = typeNode.get(CALLBACK_DATA_KEY);
        if(Objects.isNull(dataNode)){
            log.warn("视频录像结果数据解析为空");
            return;
        }
        RecordTaskVideoResult recordTaskVideoResult = null;
        try {
            recordTaskVideoResult = objectMapper.readValue(dataNode.traverse(), RecordTaskVideoResult.class);
        } catch (IOException e) {
            log.error("Parse record task video data error", e);
        }
        if(Objects.isNull(recordTaskVideoResult)){
            return;
        }
        // 保存录像视频数据
        RecordTaskVideoInDTO recordTaskVideoInDTO = new RecordTaskVideoInDTO();
        recordTaskVideoInDTO.setRecordTaskId(recordTaskVideoResult.getRecordTaskId());
        recordTaskVideoInDTO.setVideoName(recordTaskVideoResult.getVideoName());
        recordTaskVideoInDTO.setVideoUri(recordTaskVideoResult.getVideoUri());
        recordTaskVideoInDTO.setVideoSize(recordTaskVideoResult.getVideoSize());
        recordTaskVideoInDTO.setIndex(recordTaskVideoResult.getIndex());
        missionVideoService.saveMissionRecordVideo(recordTaskVideoInDTO);
    }

    private Optional<String> getAccountName(String accountId) {
        if (StringUtils.hasText(accountId)){
            Result<List<AccountOutDO>> result = accountServiceClient.listAccountInfos(Collections.singletonList(accountId));
            if(result.isOk()){
                return result.getData()
                        .stream()
                        .map(AccountOutDO::getAccount).findFirst();
            }
        }
        return Optional.empty();
    }

    @Data
    public static class CallbackResult {
        private String code;
        private String msg;
        private String notifyId;
        private String extParam;
    }

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class UosFileComposeResult extends CallbackResult {
        /**
         * 账号ID
         */
        private String accountId;
        /**
         * 上传token
         */
        private String uploadToken;
        /**
         * 源文件名称
         */
        private String srcFilename;
    }

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class VideoFrameResult extends CallbackResult {
        /**
         * 图片数量，即第几张图片
         */
        private int imageCount;
        /**
         * 图片名称
         */
        private String imageName;
        /**
         * 当前帧对应视频时间，单位：ms
         */
        private int timestamp;
        /**
         * 抽帧图片地址
         */
        private String imageUri;
        /**
         * 抽帧缩略图地址
         */
        @Setter
        private String thumbUri;
    }

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = true)
    public static class RecordTaskVideoResult extends CallbackResult {
        /**
         * 录像任务ID
         */
        private String recordTaskId;
        /**
         * 录像视频索引
         */
        private Integer index;
        /**
         * 录像视频名称
         */
        private String videoName;
        /**
         * 录像视频大小，单位：字节
         */
        private Long videoSize;
        /**
         * 录像视频存储地址
         */
        private String videoUri;
    }

}
