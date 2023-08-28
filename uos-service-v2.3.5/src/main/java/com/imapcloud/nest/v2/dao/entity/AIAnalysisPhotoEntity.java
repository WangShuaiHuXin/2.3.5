package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 识别分析-AI识别图片表实体
 * @author vastfy
 * @date 2022-11-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_ai_analysis_photo")
public class AIAnalysisPhotoEntity extends GenericEntity {

    /**
     * 识别记录ID
     */
    private String recordId;

    /**
     * 识别任务ID
     */
    private String taskId;

    /**
     * 识别任务ID
     */
    private String aiTaskId;

    /**
     * 分析数据详情ID
     */
    private Long centerDetailId;

    /**
     * 图片名称
     */
    private String photoName;

    /**
     * 图片存储路径（冗余图片路径信息）
     */
    private String photoPath;

    /**
     * 图片识别失败原因
     */
    private String reason;

    /**
     * 图片名称
     */
    private Integer state;

    /**
     * 乐观锁版本号
     */
    private Integer version;

}
