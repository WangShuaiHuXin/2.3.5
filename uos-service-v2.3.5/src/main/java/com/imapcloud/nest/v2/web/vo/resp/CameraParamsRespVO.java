package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

@Data
public class CameraParamsRespVO {
    private String cameraName;
    private Double focalLength;
    private Double sensorWidth;
    private Double sensorHeight;
    private Double pixelSizeWidth;
    private Integer batteryLifeTime;
    private Double focalLengthMin;
    private Double focalLengthMax;
    private Integer infraredMode;
}
