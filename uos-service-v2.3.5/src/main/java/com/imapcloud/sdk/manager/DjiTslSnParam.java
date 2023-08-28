package com.imapcloud.sdk.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DjiTslSnParam {
    private String dockSn;
    private String uavSn;
    private String rcSn;
    private Integer cameraType;
}
