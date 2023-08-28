package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseUavInfoOutDTO implements Serializable {
    private String appId;

    private String nestId;
    /**
     * 无人机ID
     */
    private String uavId;


    /**
     * 无人机序列号
     */
    private String uavNumber;

    /**
     * 遥控器序列号
     */
    private String rcNumber;

    /**
     * 相机名称
     */
    private String cameraName;

    /**
     * 推拉流信息
     */
    private String streamId;

    /**
     * 无人机型号值
     */
    private String type;

    /**
     * 无人机标识
     */
    private Integer which;
}
