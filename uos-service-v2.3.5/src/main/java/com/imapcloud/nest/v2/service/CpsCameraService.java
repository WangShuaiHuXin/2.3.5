package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.CpsCameraInDTO;

import java.util.List;
import java.util.Map;

public interface CpsCameraService {
    /**
     * camera-获取拍照/录像源类型设置
     *
     * @param nestId
     * @return
     */
    Map<String, Object> getCameraTypes(String nestId);

    /**
     * camera-设置拍照/录像源类型
     * @param inDTO
     */
    void setCameraTypes(CpsCameraInDTO.CpsCameraSetTypeInDTO inDTO);

    /**
     * 设置变焦
     * @param inDTO
     */
    void setCameraZoom(CpsCameraInDTO.CpsCameraSetZoomInDTO inDTO);
}
