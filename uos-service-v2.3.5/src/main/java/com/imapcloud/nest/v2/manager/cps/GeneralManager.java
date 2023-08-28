package com.imapcloud.nest.v2.manager.cps;

import com.imapcloud.nest.v2.manager.dataobj.in.GeneralManagerInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.GeneralManagerOutDO;

import java.util.List;

/**
 * 常规设置
 *
 * @author boluo
 * @date 2022-08-24
 */
public interface GeneralManager {

    /**
     * 设置基站无人机的推流地址
     *
     * @param nestId  基站id
     * @param pushUrl 推流地址
     * @return boolean
     */
    boolean setRtmpUrl(String nestId, String pushUrl, Integer uavWhich);

    /**
     * 获取无人机推流地址
     *
     * @param nestId 巢id
     * @return {@link String}
     */
    String getRtmpUrl(String nestId,Integer uavWhich);

    /**
     * 设置基站监控的推流信息
     *
     * @param pushStreamInfoInDO 推流信息
     * @return boolean
     */
    boolean setPushStreamInfo(GeneralManagerInDO.PushStreamInfoInDO pushStreamInfoInDO);

    /**
     * 组件序列号
     *
     * @param nestId 巢id
     * @return {@link GeneralManagerOutDO.ComponentSerialNumberOutDO}
     */
    GeneralManagerOutDO.ComponentSerialNumberOutDO listComponentSerialNumber(String nestId);

    /**
     * 基站摄像头信息
     *
     * @param nestId 巢id
     * @return {@link GeneralManagerOutDO.NestCameraInfoOutDO}
     */
    List<GeneralManagerOutDO.NestCameraInfoOutDO> listNestCameraInfos(String nestId);
}
