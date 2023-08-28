package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.AircraftEntity;
import com.imapcloud.nest.pojo.dto.InfraredTestTempeDto;
import com.imapcloud.nest.pojo.dto.IntelligentShutdownDTO;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.service.dto.out.CameraParamsOutDTO;

import java.util.Map;

/**
 * <p>
 * 飞机信息表 服务类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
public interface AircraftService extends IService<AircraftEntity> {
    /**
     * 软删除
     *
     * @param nestId
     * @return
     */
    int softDelete(Integer nestId);

    /**
     * 通过机巢id查询无人机id
     *
     * @param nestId
     * @return
     */
    Integer getIdByNestId(Integer nestId);

    void deleteByNestId(Integer nestId);

    /**
     * 获取相机名称
     *
     * @param nestId
     * @return
     */
    String getCameraNameByNestId(Integer nestId);


    /**
     * 查询飞机Code通过基站Uuid
     *
     * @param nestUuid
     * @return
     */
    @Deprecated
    String getAirCodeByNestUuidCache(String nestUuid);

    /**
     * 获取相机参数通过nestId
     *
     * @param
     * @return
     */
    CameraParamsOutDTO getCameraParamByNestId(String nestOrAppId, Integer mold);

    String getAirCodeByDeviceId(String deviceId);

    RestRes setAirMaxFlyAlt(String nestId, Integer maxFlyAlt);

    RestRes getAirMaxFlyAlt(String nestId);

    RestRes setVideoStreamInfraredInfo(String nestId, Boolean enable);

    RestRes getVideoStreamInfraredInfo(String nestId);

    RestRes h20selectPhotoVideoSource(Map<String, Object> param);

    RestRes setRTHAltitude(String nestId, Integer rthAlt);

    RestRes getRTHAltitude(String nestId);

    RestRes setLowBatteryIntelligentShutdown(IntelligentShutdownDTO intelligentShutdownDTO);

    RestRes getLowBatteryIntelligentShutdown(String nestId);

    RestRes setLowBatteryIntelligentGoHome(String nestId, Boolean enable);

    RestRes getMaxFarDistanceRadius(String nestId);

    RestRes setMaxFarDistanceRadius(String nestId, Integer radius);

    RestRes g900SwitchLiveVideoSource(Map<String, String> param);

    RestRes infraredAreaOrPointTestTemperature(InfraredTestTempeDto dto);

    RestRes setCameraLensVideoSource(String nestId, Integer source);

    /**
     * 获取视频字幕状态
     *
     * @param nestId
     * @return
     */
    RestRes getVideoCaptionsState(String nestId);

    /**
     * 开关视频字幕
     * @param nestId
     * @param enable
     * @return
     */
    RestRes switchVideoCaptions(String nestId, Integer enable);
}
