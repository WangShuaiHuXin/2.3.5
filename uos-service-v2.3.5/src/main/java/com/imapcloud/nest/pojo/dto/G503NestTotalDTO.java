package com.imapcloud.nest.pojo.dto;

import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Builder
@Data
public class G503NestTotalDTO {

    private String nestState;

    private String name;

    /**
     * 舱门A状态
     */
    private Integer cabinA ;

    /**
     * 舱门A状态
     */
    private Integer cabinB;

    /**
     * 平台
     * 10 1号平台朝下
     * 11 1号平台朝上
     * 20 2号平台朝下
     * 21 2号平台朝上
     * 30 3号平台朝下
     * 31 3号平台朝上
     */
    private String platform;

    /**
     * x归中状态
     * -1 -> 未知
     * 0 -> 收紧
     * 1 -> 释放
     */
    private Integer squareX;

    /**
     * y归中状态
     * -1 -> 未知
     * 0 -> 收紧
     * 1 -> 释放
     */
    private Integer squareY;

    /**
     * 室内温度
     */
    private Double insideTemperature;

    private Integer mqttServerConnected;

    private List<G503NestBatteryInfoDTO> nestBatteryInfoList;

    private Map<String, G503NestUavInfoDTO> g503NestUavInfoMap;

    private G503NestUavInfoDTO g503NestUavInfoDTO;

    public G503NestTotalDTO() {
        this.nestState = NestStateEnum.OFF_LINE.getChinese();
        this.name = "";
        this.cabinA = -1;
        this.cabinB = -1;
        this.platform = "00";
        this.squareX = -1;
        this.squareY = -1;
        this.insideTemperature = 0.0;
        this.mqttServerConnected = 0;
        this.nestBatteryInfoList = Collections.emptyList();
        this.g503NestUavInfoMap = Collections.emptyMap();
    }
}
