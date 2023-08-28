package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.model.DefectInfoEntity;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.StationInfraredRecordRectangleEntity;
import lombok.Data;

import java.util.List;

@Data
public class StationInfraredRecordDto extends MissionPhotoEntity {
    /**
     * 最高温度
     */
    private Double maxTemperature;

    /**
     * 平均温度
     */
    private Double avgTemperature;

    /**
     * 最低温度
     */
    private Double minTemperature;

    /**
     * 可见光地址
     */
    private String sunUrl;

    /**
     * 融合地址
     */
    private String pipUrl;

    /**
     * 识别地址
     */
    private String recordUrl;

    /**
     * 是否有问题
     */
    private Integer flag;

    /**
     * 标签id
     */
    private Integer tagId;

    /**
     * StationInfraredRecord的id
     */
    private Integer sid;

    /**
     * 红外手动标记详细信息List
     */
    private List<StationInfraredRecordRectangleEntity> stationInfraredRecordRectangleEntities;
}
