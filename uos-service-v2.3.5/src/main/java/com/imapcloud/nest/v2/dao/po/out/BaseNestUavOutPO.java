package com.imapcloud.nest.v2.dao.po.out;

import lombok.Data;

@Data
public class BaseNestUavOutPO {


    private String nestId;
    /**
     * 无人机ID
     */
    private String uavId;

    /**
     * 无人机型号
     */
    private String code;

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
    private Integer type;

    /**
     * 无人机标识
     */
    private Integer which;
}
