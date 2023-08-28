package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.async.CpsAudioListAsync;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.mapper.NestSensorAudioMapper;
import com.imapcloud.nest.model.NestSensorAudioEntity;
import com.imapcloud.nest.pojo.dto.AudioDto;
import com.imapcloud.nest.service.NestSensorAudioService;
import com.imapcloud.nest.service.NestSensorRelService;
import com.imapcloud.nest.utils.PageUtils;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.AudioTranscodeException;
import com.imapcloud.nest.v2.common.exception.FileIOReadException;
import com.imapcloud.nest.v2.common.exception.FileUploadException;
import com.imapcloud.nest.v2.common.exception.UosServiceErrorCode;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileStorageOutDO;
import com.imapcloud.nest.v2.manager.rest.UploadManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.base.BaseManager;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.utils.JSONUtil;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * <p>
 * 机巢喊话器的音频表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2021-04-06
 */
@Slf4j
@Service
public class NestSensorAudioServiceImpl extends ServiceImpl<NestSensorAudioMapper, NestSensorAudioEntity> implements NestSensorAudioService {

    @Autowired
    private CpsAudioListAsync cpsAudioListAsync;

    @Autowired
    private NestSensorRelService nestSensorRelService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private UploadManager uploadManager;

    @Override
    public RestRes selectListPage(String nestId, String audioName, Integer page, Integer limit) {
        IPage<NestSensorAudioEntity> totalPage = baseMapper.getListPage(new Page<>(page, limit), nestId, audioName);
        Map<String, Object> map = new HashMap<>(2);
        map.put("page", new PageUtils(totalPage));
        return RestRes.ok(map);
    }

    @Override
    public RestRes selectAllListById(String nestId) {
        List<NestSensorAudioEntity> nestSensorAudioEntityList = baseMapper.selectList(new QueryWrapper<NestSensorAudioEntity>().eq("base_nest_id", nestId).eq("deleted", false));
        Map<String, Object> map = new HashMap<>(1);
        map.put("list", nestSensorAudioEntityList);
        return RestRes.ok(map);
    }

    @Override
    public RestRes addAudioToServer(MultipartFile file, NestSensorAudioEntity nestSensorAudioEntity) {
        String mp3Filename = StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "mp3_" + BizIdUtils.snowflakeId() + ".mp3";
        String audioName = nestSensorAudioEntity.getAudioName();
        String nestId = nestSensorAudioEntity.getNestId();
        Integer nestType = nestSensorAudioEntity.getNestType();
        nestSensorAudioEntity.setBaseNestId(nestId);
        NestSensorAudioEntity one = this.getOne(new QueryWrapper<NestSensorAudioEntity>().eq("base_nest_id", nestId).eq("deleted", false).eq("audio_name", audioName));
        if (one != null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANNOT_BE_REPEATED_AUDIO_NAME.getContent()));
        }

        Path tempMp3File;
        try {
            tempMp3File = Files.createTempFile("mp3_", ".mp3");
            file.transferTo(tempMp3File);
        } catch (IOException e) {
            log.error("创建临时文件异常", e);
            throw new BizException("创建临时文件异常");
        }

        // 上传喊话器音频mp3格式
        String fileSize = this. formatFileSize(file.getSize());
        CommonFileInDO commonFileInDO = new CommonFileInDO();
        commonFileInDO.setFileName(mp3Filename);
        try {
            commonFileInDO.setInputStream(Files.newInputStream(tempMp3File));
        } catch (IOException e) {
            throw new FileIOReadException(UosServiceErrorCode.FILE_IO_READ_ERROR.getI18nMessage(mp3Filename));
        }
        Optional<FileStorageOutDO> mp3 = uploadManager.uploadFile(commonFileInDO);
        if(!mp3.isPresent()){
            throw new FileUploadException(UosServiceErrorCode.FILE_UPLOAD_ERROR.getI18nMessage(mp3Filename));
        }
        String mp3FilePath = mp3.get().getStoragePath() + SymbolConstants.SLASH_LEFT + mp3.get().getFilename();

        // 基站类型为1/5时，需要上传pcm格式
        String pcmFilePath = null;
        if (Objects.equals(nestType, NestTypeEnum.S100_V1.getValue()) || Objects.equals(nestType, NestTypeEnum.S100_V2.getValue())){
            String pcmFilename = mp3Filename.substring(0, mp3Filename.lastIndexOf(SymbolConstants.POINT)) + ".pcm";
            try{
                File pcmFile = transcodeMp3ToPcm(mp3Filename, tempMp3File);
                fileSize = this.formatFileSize(pcmFile.length());
                commonFileInDO.setFileName(pcmFilename);
                commonFileInDO.setInputStream(Files.newInputStream(pcmFile.toPath()));
            } catch (IOException e) {
                log.error("音频转码异常", e);
                throw new FileIOReadException(UosServiceErrorCode.FILE_IO_READ_ERROR.getI18nMessage(pcmFilename));
            }
            Optional<FileStorageOutDO> pcm = uploadManager.uploadFile(commonFileInDO);
            if(!pcm.isPresent()){
                throw new FileUploadException(UosServiceErrorCode.FILE_UPLOAD_ERROR.getI18nMessage(pcmFilename));
            }
            pcmFilePath = pcm.get().getStoragePath() + SymbolConstants.SLASH_LEFT + pcm.get().getFilename();
        }
        // 保存入库
        nestSensorAudioEntity.setAudioUrl(mp3FilePath);
        nestSensorAudioEntity.setPcmUrl(pcmFilePath);
        nestSensorAudioEntity.setAudioSize(fileSize);
        nestSensorAudioEntity.setCreatorId(Long.valueOf(TrustedAccessTracerHolder.get().getAccountId()));
        this.save(nestSensorAudioEntity);
        return RestRes.ok();
    }

    @Override
    public RestRes addAudioToCps(Integer audioId) {
        NestSensorAudioEntity audioEntity = this.getById(audioId);
        String audioName = audioEntity.getAudioName();
        String nestId = audioEntity.getBaseNestId();
        String mediaDomain = geoaiUosProperties.getDomain().getMedia();
        Boolean isMp3 = audioEntity.getNestType() == 2;
        String fileUrl = isMp3 ? mediaDomain + audioEntity.getAudioUrl() : mediaDomain + audioEntity.getPcmUrl();
        // 连接机巢
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            BaseManager baseManager = cm.getBaseManager();
            log.info("准备开始上传音频到机巢");
            cm.getAccessoryManagerCf().uploadAudio(audioName, fileUrl, isMp3, (result, isSuccess, errMsg) -> {
                // 获取回调结果处理，结果ws发送给前端
                cpsAudioListAsync.dealResult(isSuccess, result, audioEntity, nestUuid);

            });

            // 获取上传状态进度，ws发送给前端
            baseManager.listenAccessoryState((accessoryStatus, isSuccess, errMsg) -> {
                if (isSuccess) {
                    Map<String, Object> map = new HashMap<>(2);
                    map.put("nestUuid", nestUuid);
                    map.put("accessoryStatus", accessoryStatus);
                    String message = WebSocketRes.ok().topic(WebSocketTopicEnum.UPLOAD_AUDIO_TO_CPS).data(map).toJSONString();
                    // 推流
                    ChannelService.sendMessageByType8Channel(nestUuid, message);
                }
            });

            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_COMMAND_SENT.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UPLOADING_AUDIO_TO_MACHINE_NEST_REQUEST_SENDING_FAILED.getContent()));
    }

    @Override
    public RestRes updateAudio(NestSensorAudioEntity nestSensorAudioEntity) {
        // 只是传到服务器的音频，直接进行重命名即可
        nestSensorAudioEntity.setBaseNestId(nestSensorAudioEntity.getNestId());
        baseMapper.updateById(nestSensorAudioEntity);
        return RestRes.ok();
    }

    @Override
    public RestRes deleteAudio(List<Integer> ids) {
        if (ids != null && ids.size() > 0) {
            baseMapper.deleteBatchIds(ids);
            return RestRes.ok();
        }
        return RestRes.err("请选择要删除的音频数据");
    }

    @Override
    public RestRes stopUploadAudio(String nestId) {
        // 连接机巢
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getAccessoryManagerCf().stopUploadAudio();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CANCEL_UPLOAD.getContent()));
            }
            return RestRes.err("取消上传失败," + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes resetSpeaker(String nestId) {
        // 连接机巢
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getAccessoryManagerCf().resetSpeaker();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_RESET.getContent()));
            }
            return RestRes.err("重置失败," + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public Map<String, Object> getSpeakerAudioList(String nestId) {
        Map<String, Object> map = new HashMap<>(4);
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<BaseResult3> res = cm.getAccessoryManagerCf().listSpeakerPlay();
            if (res.isSuccess()) {
                BaseResult3 br3 = res.getRes();
                List<AudioDto> audioDtos = JSONUtil.parseArray(br3.getParam(), AudioDto.class);
                map.put("audioList", audioDtos);
                map.put("success", true);
                map.put("msg", "success");
                return map;
            }
            map.put("success", false);
            map.put("msg", res.getMsg());
            return map;
        }
        map.put("success", false);
        map.put("msg", MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        return map;
    }

    @Override
    public RestRes startRecord(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getAccessoryManagerCf().speakerStartRecord(false, false);
            // 发送ws给前端获取喊话器状态
            return uploadAudioToCpsWs(nestId, cm, res);
        }
        return RestRes.err("开始录音失败,机巢离线");
    }

    @Override
    public RestRes endRecord(String nestId, Long fileSize) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getAccessoryManagerCf().speakerEndRecord();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_END_RECORDING.getContent()));
            }
            return RestRes.err("结束录音失败," + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes recordAudio(byte[] bytes, String nestId) {
        log.info("准备上传音频数据，byte[]长度： " + bytes.length);
        try {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                // 发送音频数据字节到机巢
                CompletableFuture<Boolean> future = new CompletableFuture<>();
                cm.getAccessoryManager().sendAudioBytes(bytes, (result, isSuccess, errMsg) -> {
                    if (isSuccess) {
                        future.complete(result);
                    } else {
                        future.complete(false);
                        log.info("byte失败");
                    }
                });

                try {
                    if (future.get(5, TimeUnit.SECONDS)) {
                        return RestRes.ok();
                    }
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    return RestRes.err("发送数据失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RestRes.err("发送数据失败");
        }

        return RestRes.err("发送数据失败");
    }

    @Override
    public Integer updateNameByNestIdAndIndex(Integer nestId, Integer index, String name) {
        return baseMapper.updateNameByNestIdAndIndex(nestId, index, name);
    }

    @Override
    public RestRes listAudioFromAircraft(String nestId) {
        if (nestId != null) {
            // 获取机巢音频列表
            Map<String, Object> audioResultMap = this.getSpeakerAudioList(nestId);
            boolean success = (boolean) audioResultMap.get("success");
            if (!success) {
                String errorMsg = (String) audioResultMap.get("msg");
                return RestRes.err(errorMsg);
            }

            // 获取机巢音频列表成功
            Map<String, Object> map = new HashMap<>(2);
            List<AudioDto> speakerAudioList = (List<AudioDto>) audioResultMap.get("audioList");
            map.put("audioList", speakerAudioList);
            return RestRes.ok(map);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes delete(String nestId, String index) {
        // 连接机巢
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            List<Integer> indexList = Arrays.asList(index.split(",")).stream().map(e -> Integer.parseInt(e)).collect(Collectors.toList());
            MqttResult<NullParam> res = cm.getAccessoryManagerCf().speakerDeleteAudio(indexList);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_DELETED.getContent()));
            }
            return RestRes.err("删除失败," + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes setSpeakerVolume(Map<String, String> param) {
        if (param != null) {
            String nestId = param.get("nestId");
            Integer volume = Integer.valueOf(param.get("volume"));
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getAccessoryManagerCf().setSpeakerVolume(volume);
                if (res.isSuccess()) {
                    nestSensorRelService.updateVolumeByNestId(volume, nestId);
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_VOLUME_SETTING.getContent()));
                }
                return RestRes.err("音量设置失败," + res.getMsg());
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes setIfRepeatPlay(Map<String, String> param) {
        String res = "";
        if (param != null) {
            String repeat = param.get("repeat");
            String nestId = param.get("nestId");
            boolean isRepeat = Objects.equals(repeat, "1");
            res = isRepeat ? "设置" : "取消";
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<NullParam> mqttRes = cm.getAccessoryManagerCf().setSpeakerPlayMode(isRepeat);
                if (mqttRes.isSuccess()) {
                    return RestRes.ok(res + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_REPLAY_SUCCESS.getContent()));
                }
                return RestRes.err(res + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_REPLAY_FAILURE.getContent()) + mqttRes.getMsg());
            }
        }
        return RestRes.err(res + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes speakerPlayAudio(Map<String, Object> param) {
        if (param != null) {
            String nestId = String.valueOf(param.get("nestId"));
            Integer index = (Integer) param.get("index");
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getAccessoryManagerCf().speakerPlayAudio(index);
                if (res.isSuccess()) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_PLAY.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLAYBACK_FAILED.getContent()) + res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes speakerStopAudio(String nestId) {
        if (nestId != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getAccessoryManagerCf().speakerStopPlayAudio();
                if (res.isSuccess()) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_STOP_PLAY.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_STOP_PLAYBACK.getContent()) + res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes speakerPlayListRename(Map<String, Object> param) {
        if (param != null) {
            Integer index = (Integer) param.get("index");
            String name = (String) param.get("fileName");
            String nestId = (String) param.get("nestId");
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getAccessoryManagerCf().speakerPlayListRename(index, name);
                if (res.isSuccess()) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_RENAME.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RENAME_FAILURE.getContent()) + res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes openBeacon(String nestId) {
        if (nestId != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getAccessoryManagerCf().openBeacon();
                if (res.isSuccess()) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_TURN_ON_NIGHTLIGHT.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE_TO_TURN_ON_THE_NIGHT_LIGHT.getContent()) + res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE_TO_TURN_ON_THE_NIGHT_LIGHT.getContent()));
    }

    @Override
    public RestRes closeBeacon(String nestId) {
        if (nestId != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getAccessoryManagerCf().closeBeacon();
                if (res.isSuccess()) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CLOSE_NIGHTLIGHT.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE_TO_TURN_OFF_NIGHT_NAVIGATION_LIGHTS.getContent()) + res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE_TO_TURN_OFF_NIGHT_NAVIGATION_LIGHTS.getContent()));
    }

    @Override
    public RestRes openSpotlight(String nestId) {
        if (nestId != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getAccessoryManagerCf().openSpotlight();
                if (res.isSuccess()) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_TURN_ON_SEARCHLIGHT.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE_TURN_ON_SEARCHLIGHT.getContent()) + res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes closeSpotlight(String nestId) {
        if (nestId != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getAccessoryManagerCf().closeSpotlight();
                if (res.isSuccess()) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CLOSE_SEARCHLIGHT.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE_TURN_OFF_THE_SEARCHLIGHT.getContent()) + res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE_TURN_OFF_THE_SEARCHLIGHT.getContent()));
    }

    @Override
    public RestRes setSpotlightBrightness(Map<String, String> param) {
        if (param != null) {
            Integer brightness = Integer.valueOf(param.get("brightness"));
            String nestId = param.get("nestId");
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getAccessoryManagerCf().setSpotlightBrightness(brightness);
                if (res.isSuccess()) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_SET_SEARCHLIGHT_BRIGHTNESS.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SET_SEARCHLIGHT_BRIGHTNESS.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SET_SEARCHLIGHT_BRIGHTNESS.getContent()));
    }

    @Override
    public RestRes recordAudio(MultipartFile file, String nestId) {
        log.info("file的文件大小： " + file.getSize());
        if (file.getSize() > 36864) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SEND_DATA__EXCEEDS_MAXIMUM_VALUE.getContent()));
        }
        try {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                CompletableFuture<Boolean> future = new CompletableFuture<>();
                log.info("准备发送音频到机巢.....");
                cm.getAccessoryManager().sendAudioBytes(file.getBytes(), (result, isSuccess, errMsg) -> {
                    if (isSuccess) {
                        log.info("成功发送音频到机巢.....");
                        future.complete(result);
                    } else {
                        log.info("失败发送音频到机巢.....");
                        future.complete(false);
                    }
                });
                try {
                    if (future.get(5, TimeUnit.SECONDS)) {
                    }
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SEND_DATA.getContent()));
                }
            }
            return RestRes.ok();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SEND_DATA.getContent()));
        }
    }

    // 发送ws给前端获取喊话器状态
    private RestRes uploadAudioToCpsWs(String nestId, ComponentManager cm, MqttResult<NullParam> res) {
        if (res.isSuccess()) {
            BaseManager baseManager = cm.getBaseManager();
            String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
            // 获取上传状态，ws发送给前端
            baseManager.listenAccessoryState((accessoryStatus, isSuccess, errMsg) -> {
                if (isSuccess) {
                    Map<String, Object> map = new HashMap<>(2);
                    map.put("nestUuid", nestUuid);
                    map.put("accessoryStatus", accessoryStatus);
                    String message = WebSocketRes.ok().topic(WebSocketTopicEnum.UPLOAD_AUDIO_TO_CPS).data(map).toJSONString();
                    // 推流
                    ChannelService.sendMessageByType8Channel(nestUuid, message);
                }
            });

            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_START_RECORDING.getContent()));
        } else {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_START_RECORDING.getContent()) + res.getMsg());
        }
    }


//    // 将mp3转化为pcm
//    private boolean convertMP3ToPcm(String mp3filepath, String pcmfilepath) {
//        try {
//            // 获取文件的音频流，pcm的格式
//            AudioInputStream audioInputStream = getPcmAudioInputStream(mp3filepath);
//            // 将音频转化为 pcm的格式保存下来
//            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(pcmfilepath));
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            log.info("转化音频文件为pcm格式失败");
//            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_UPLOAD.getContent()));
//        }
//    }

    private File transcodeMp3ToPcm(String mp3Filename, InputStream mp3InputStream) throws IOException {
        Path tempPcmPath = Files.createTempFile("pcm_", ".pcm");
        try(AudioInputStream audioInputStream = getPcmInputStream(mp3Filename, mp3InputStream); OutputStream outputStream = Files.newOutputStream(tempPcmPath)) {
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputStream);
            return tempPcmPath.toFile();
        }
    }
    private File transcodeMp3ToPcm(String mp3Filename, Path tempMp3File) throws IOException {
        Path tempPcmPath = Files.createTempFile("pcm_", ".pcm");
        try(AudioInputStream audioInputStream = getPcmInputStream(mp3Filename, tempMp3File)) {
//        try(AudioInputStream audioInputStream = getPcmInputStream(mp3Filename, tempMp3File); OutputStream outputStream = Files.newOutputStream(tempPcmPath)) {
//            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputStream);
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, tempPcmPath.toFile());
            return tempPcmPath.toFile();
        }
    }


    /**
     * 获取文件的音频流
     */
    private AudioInputStream getPcmInputStream(String mp3Filename, Path tempMp3File) {
        AudioInputStream audioInputStream;
        AudioFormat targetFormat;
        AudioInputStream in;
        try {

            // 读取音频文件的类
            MpegAudioFileReader mp = new MpegAudioFileReader();
            in = mp.getAudioInputStream(tempMp3File.toFile());
            AudioFormat baseFormat = in.getFormat();

            // 设定输出格式为pcm格式的音频文件
            targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16,
                    baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);

            // 输出到音频
            audioInputStream = AudioSystem.getAudioInputStream(targetFormat, in);

        } catch (Exception e) {
            log.error("获取音频文件流失败", e);
            throw new AudioTranscodeException(UosServiceErrorCode.AUDIO_TRANSCODE_ERROR.getI18nMessage(mp3Filename));
        }
        return audioInputStream;
    }
    /**
     * 获取文件的音频流
     */
    private AudioInputStream getPcmInputStream(String mp3Filename, InputStream mp3InputStream) {
        AudioInputStream audioInputStream;
        AudioFormat targetFormat;
        AudioInputStream in;
        try {

            // 读取音频文件的类
            MpegAudioFileReader mp = new MpegAudioFileReader();
            in = mp.getAudioInputStream(mp3InputStream);
            AudioFormat baseFormat = in.getFormat();

            // 设定输出格式为pcm格式的音频文件
            targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16,
                    baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);

            // 输出到音频
            audioInputStream = AudioSystem.getAudioInputStream(targetFormat, in);

        } catch (Exception e) {
            log.error("获取音频文件流失败", e);
            throw new AudioTranscodeException(UosServiceErrorCode.AUDIO_TRANSCODE_ERROR.getI18nMessage(mp3Filename));
        }
        return audioInputStream;
    }
//    /**
//     * 获取文件的音频流
//     *
//     * @param mp3filepath
//     * @return
//     */
//    private AudioInputStream getPcmAudioInputStream(String mp3filepath) {
//        File mp3 = new File(mp3filepath);
//        AudioInputStream audioInputStream = null;
//        AudioFormat targetFormat = null;
//        AudioInputStream in = null;
//        try {
//
//            // 读取音频文件的类
//            MpegAudioFileReader mp = new MpegAudioFileReader();
//            in = mp.getAudioInputStream(mp3);
//            AudioFormat baseFormat = in.getFormat();
//
//            // 设定输出格式为pcm格式的音频文件
//            targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16,
//                    baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
//
//            // 输出到音频
//            audioInputStream = AudioSystem.getAudioInputStream(targetFormat, in);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info("获取音频文件流失败");
//            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_UPLOAD.getContent()));
//        }
//        return audioInputStream;
//    }

    private String formatFileSize(long fileS) {

        DecimalFormat df = new DecimalFormat("#.0");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }
}
