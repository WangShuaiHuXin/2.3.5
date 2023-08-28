package com.imapcloud.nest.v2.service.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CameraParamsOutDTO {
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
