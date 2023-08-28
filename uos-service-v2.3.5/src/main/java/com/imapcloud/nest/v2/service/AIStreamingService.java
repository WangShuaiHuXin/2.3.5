package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.service.dto.out.AIAnalysisRepoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestAiStreamingInfoOutDTO;

import java.util.List;

/**
 * AI视频流业务接口
 *
 * @author Vastfy
 * @date 2022/12/24 10:47
 * @since 2.1.7
 */
public interface AIStreamingService {

    /**
     * 获取登录用户支持视频流识别的识别功能信息
     * @return  识别功能信息
     */
    List<AIAnalysisRepoOutDTO> getGrantedStreamingRecFunctions();

    /**
     * 获取基站无人机推流AI拉流信息
     * @param nestId    基站ID
     * @param which 平台编号，为空时会返回所有拉流信息
     * @return  基站AI流拉流信息
     */
    List<NestAiStreamingInfoOutDTO> getAiStreamingInfo(String nestId, Integer which);

    /**
     * 开启AI流识别
     * @param data  开启信息
     * @return  基站AI流拉流信息
     */
    NestAiStreamingInfoOutDTO openAiStreaming(AiStreamingOpenInDTO data);

    /**
     * 退出AI流识别
     * @param data  退出信息
     * @return 已退出的视频AI识别信息
     */
    NestAiStreamingInfoOutDTO exitAiStreaming(AiStreamingExitInDTO data);

    /**
     * 终止AI流识别
     * @param nestId 基站ID，不能为空
     * @param which 平台编号，不能为空
     */
    void terminateAiStreaming(String nestId, Integer which);

    /**
     * 切换AI流识别模式
     * @param data  退出信息
     * @return 切换后的视频AI识别信息
     */
    NestAiStreamingInfoOutDTO switchAiStreaming(AiStreamingSwitchInDTO data);

    /**
     * 超时终结视频AI识别
     * @param data 超时终结数据
     */
    void timeoutTerminateAiStreaming(AiStreamingTerminateInDTO data);

    /**
     * AI流识别告警配置
     * @param data  配置信息
     */
    void setAiStreamingAlarmSettings(AiStreamingAlarmSettingInDTO data);

}
