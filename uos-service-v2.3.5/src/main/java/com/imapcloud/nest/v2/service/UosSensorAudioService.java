package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.out.AudioOutDTO;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosSensorAudioService.java
 * @Description UosSensorAudioService
 * @createTime 2022年08月17日 16:13:00
 */
public interface UosSensorAudioService {

    /**
     * 删除喊话器音频
     * @param audioId
     * @return
     */
    boolean deleteAudio(String nestId , Integer audioId);

    /**
     *  重置喊话器
     * @param nestId
     * @return
     */
    boolean resetAudio(String nestId);

    /**
     * 获取喊话器列表
     * @param nestId
     * @return
     */
    List<AudioOutDTO> getAudioList(String nestId);

}
