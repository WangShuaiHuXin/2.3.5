package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaDetailEntity.java
 * @Description DataPanoramaDetailEntity
 * @createTime 2022年09月16日 11:27:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_panorama_detail")
public class DataPanoramaDetailEntity extends GenericEntity {

    /**
     * 全景明细主键
     */
    private String detailId;

    /**
     * 全景明细数据路径-记录目录位置
     */
    private String detailUrl;

    /**
     * 全景点主键
     */
    private String pointId;

    /**
     * 架次id
     */
    private String missionId;

    /**
     * 任务架次记录id
     */
    private String missionRecordsId;

    /**
     * 航线id
     */
    private String airLineId;

    /**
     * 航点id
     */
    private String airPointId;

    /**
     * 架次号
     */
    private String missionFlyIndex;

    /**
     * 任务架次开始时间
     */
    private LocalDateTime missionRecordTime;

    /**
     * 照片采集时间
     */
    private LocalDateTime acquisitionTime;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 基站id
     */
    private String baseNestId;
}
