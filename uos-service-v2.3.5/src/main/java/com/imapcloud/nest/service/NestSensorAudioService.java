package com.imapcloud.nest.service;

import com.imapcloud.nest.model.NestSensorAudioEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.pojo.dto.AudioDto;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 机巢喊话器的音频表 服务类
 * </p>
 *
 * @author zheng
 * @since 2021-04-06
 */
public interface NestSensorAudioService extends IService<NestSensorAudioEntity> {


    RestRes selectListPage(String nestId, String audioName, Integer pageNum, Integer pageSize);

    /**
     * 获取机巢的音频列表
     *
     * @param nestId
     * @return
     */
    RestRes selectAllListById(String nestId);

    /**
     * 上传音频到服务器
     *
     * @param file
     * @param fileInfoEntity
     * @return
     */
    RestRes addAudioToServer(MultipartFile file, NestSensorAudioEntity fileInfoEntity);

    /**
     * 删除音频到cps
     *
     * @param audioId
     * @return
     */
    RestRes addAudioToCps(Integer audioId);

    /**
     * 音频重命名
     *
     * @param fileInfoEntity
     * @return
     */
    RestRes updateAudio(NestSensorAudioEntity fileInfoEntity);

    /**
     * 删除音频
     *
     * @param ids
     * @return
     */
    RestRes deleteAudio(List<Integer> ids);

    /**
     * 取消音频传输（包含实时语音、上传音频）
     *
     * @param nestId
     * @return
     */
    RestRes stopUploadAudio(String nestId);

    /**
     * 喊话器状态重置
     *
     * @param nestId
     * @return
     */
    RestRes resetSpeaker(String nestId);

    /**
     * 获取机巢喊话器音频列表
     *
     * @param nestId
     * @return
     */
    Map<String, Object> getSpeakerAudioList(String nestId);

    /**
     * 开始录音
     *
     * @param nestId
     * @return
     */
    RestRes startRecord(String nestId);

    /**
     * 结束录音
     *
     * @param nestId
     * @return
     */
    RestRes endRecord(String nestId, Long fileSize);

    /**
     * 上传实时录音
     *
     * @param file
     * @param nestId
     * @return
     */
    RestRes recordAudio(MultipartFile file, String nestId);

    RestRes recordAudio(byte[] bytes, String nestId);

    Integer updateNameByNestIdAndIndex(Integer nestId, Integer index, String name);

    RestRes listAudioFromAircraft(String nestId);

    RestRes delete(String nestId, String index);

    RestRes setSpeakerVolume(Map<String, String> param);

    RestRes setIfRepeatPlay(Map<String, String> param);

    RestRes speakerPlayAudio(Map<String, Object> param);

    RestRes speakerStopAudio(String nestId);

    RestRes speakerPlayListRename(Map<String, Object> param);

    RestRes openBeacon(String nestId);

    RestRes closeBeacon(String nestId);

    RestRes openSpotlight(String nestId);

    RestRes closeSpotlight(String nestId);

    RestRes setSpotlightBrightness(Map<String, String> param);
}
