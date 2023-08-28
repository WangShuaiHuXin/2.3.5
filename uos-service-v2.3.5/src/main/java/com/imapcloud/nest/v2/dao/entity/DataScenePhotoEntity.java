package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 现场取证
 *
 * @author boluo
 * @date 2022-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_scene_photo")
public class DataScenePhotoEntity extends GenericEntity {

    /**
     * 截图id（现场取证）
     */
    private Long scenePhotoId;

    /**
     * 原图路径
     */
    private String srcImagePath;

    /**
     * 缩略图路径
     */
    private String thumbnailImagePath;

    /**
     * 截图名称
     */
    private String scenePhotoName;

    /**
     * 地址信息
     */
    private String addr;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 架次id
     */
    private Long missionId;

    /**
     * 架次记录id
     */
    private Long missionRecordsId;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 问题等级
     */
    private Long topicLevelId;

    /**
     * 行业
     * @deprecated 2.1.4，使用industryType代替
     */
    @Deprecated
    private Long topicIndustryId;

    private Integer industryType;

    /**
     * 问题类型
     */
    private Long topicProblemId;
}
