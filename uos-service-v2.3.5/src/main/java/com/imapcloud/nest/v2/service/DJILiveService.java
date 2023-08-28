package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.out.DJICommonResultOutDTO;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJILiveService.java
 * @Description DJILiveService
 * @createTime 2022年10月19日 15:52:00
 */
public interface DJILiveService {

    /**
     * 设置视频分辨率
     * @param type
     * @param videoId
     * @param quality
     */
    void setVideoQuality(Integer type , String uuid , String videoId , Integer quality);

    /**
     * 开启/关闭 直播
     * @param uuid
     * @param type
     * @param open
     * @return
     */
    DJICommonResultOutDTO.LiveResultOutDTO livePush(String uuid ,Integer type,  Boolean open , String videoId , Integer quality);

    /**
     * 刷新直播
     * @param uuid
     * @param type
     * @return
     */
    DJICommonResultOutDTO.LiveResultOutDTO flushLivePush(String uuid, Integer type,String videoId , Integer quality);

    /**
     * 图传增强切换
     * @param uuid
     * @param open
     * @return
     */
    void  switchSdrWorkMode(String uuid,Boolean open);

    /**
     * 判断是否可以开启直播
     * @param uuid
     * @param videoId
     * @return
     */
    boolean isStreamPush(String uuid , String videoId);

    }
