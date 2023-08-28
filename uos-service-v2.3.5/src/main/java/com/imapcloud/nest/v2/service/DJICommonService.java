package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.ChargeLiveLensInDTO;
import com.imapcloud.nest.v2.service.dto.in.LimitDistanceInDTO;
import com.imapcloud.nest.v2.service.dto.in.LimitHeightInDTO;
import com.imapcloud.nest.v2.service.dto.out.DJICommonResultOutDTO;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJICommonService.java
 * @Description DJICommonService
 * @createTime 2022年10月19日 15:52:00
 */
public interface DJICommonService {

    /**
     * 开启/关闭DEBUG
     * @param uuid
     * @param open
     */
    DJICommonResultOutDTO.CommonResultOutDTO debugMode(String uuid , Boolean open);

    /**
     * 开启/关闭 补光灯
     * @param uuid
     * @param open
     */
    DJICommonResultOutDTO.CommonResultOutDTO supplementLight(String uuid , Boolean open);

    /**
     * 一键返航
     * @param uuid
     */
    DJICommonResultOutDTO.CommonResultOutDTO returnHome(String uuid);

    /**
     * 机场重启
     * @param uuid
     */
    DJICommonResultOutDTO.CommonResultOutDTO deviceReboot(String uuid);

    /**
     * 开启/关闭 飞行器
     * @param uuid
     * @param open
     */
    DJICommonResultOutDTO.CommonResultOutDTO drone(String uuid , Boolean open);

    /**
     * 数据格式化
     * @param uuid
     */
    DJICommonResultOutDTO.CommonResultOutDTO deviceFormat(String uuid);

    /**
     * 开启/关闭 舱盖
     * @param uuid
     * @param open
     */
    DJICommonResultOutDTO.CommonResultOutDTO cover(String uuid , Boolean open);

    /**
     * 开启/关闭 推杆
     * @param uuid
     * @param open
     */
    DJICommonResultOutDTO.CommonResultOutDTO putter(String uuid , Boolean open);

    /**
     * 开启/关闭 充电
     * @param uuid
     * @param open
     */
    DJICommonResultOutDTO.CommonResultOutDTO charge(String uuid , Boolean open);

    /**
     * 图传增强切换
     * @param nestId
     * @param open
     * @return
     */
    void  switchSdrWorkMode(String nestId,Boolean open);

    /**
     * 切换直播镜头
     * @param dto
     */
    void chargeLiveLens(ChargeLiveLensInDTO dto);

    /**
     * 无人机数据格式化
     * @param nestId
     * @return
     */
    DJICommonResultOutDTO.CommonResultOutDTO droneFormat(String nestId);

    /**
     * 限高设置
     * @param limitHeightInDTO
     */
    void limitHeight(LimitHeightInDTO limitHeightInDTO);

    /**
     * 限远设置
     * @param limitDistanceInDTO
     */
    void limitDistance(LimitDistanceInDTO limitDistanceInDTO);
}
