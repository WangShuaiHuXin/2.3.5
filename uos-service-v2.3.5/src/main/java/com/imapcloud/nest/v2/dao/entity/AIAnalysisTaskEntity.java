package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import com.imapcloud.nest.v2.common.enums.AIAnalysisTaskTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 识别分析-AI识别任务表实体
 * @author vastfy
 * @date 2022-11-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_ai_analysis_task")
public class AIAnalysisTaskEntity extends GenericEntity {

    /**
     * AI识别任务ID（第三方识别任务ID）
     */
    private String aiTaskId;

    /**
     * 识别任务ID
     */
    private String taskId;

    /**
     * 飞行架次记录数据ID
     */
    private Long centerBaseId;

    /**
     * 飞行架次任务ID
     */
    private Long flightTaskId;

    /**
     * 飞行架次标签名称
     */
    private String tagName;

    /**
     * AI分析任务名称
     */
    private String taskName;

    /**
     * 图片名称
     */
    private Integer state;

    /**
     * 分析时长（单位：秒）
     */
    private Integer costTime;

    /**
     * 合计识别图片数量
     */
    private Integer totalImageCount;

    /**
     * 图片识别成功数量
     */
    private Integer imageSuccessCount;

    /**
     * 图片识别失败数量
     */
    private Integer imageFailedCount;

    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * AI任务名称
     */
    private String aiTaskName;

    /**
     * AI识别任务类型【0：综合；1：缺陷识别；2：表计读数；3：红外测温】（取字典`GEOAI_AI_TASK_TYPE`数据项值）
     * @see AIAnalysisTaskTypeEnum
     */
    private Integer aiTaskType;

    /**
     * 是否为系统自动创建的AI识别任务
     */
    private Boolean auto;

    /**
     * 单位编码
     */
    private String orgCode;

}
