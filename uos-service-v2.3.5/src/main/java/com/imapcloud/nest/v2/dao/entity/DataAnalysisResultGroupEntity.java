package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 问题组
 *
 * @author boluo
 * @date 2022-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_analysis_result_group")
public class DataAnalysisResultGroupEntity extends GenericEntity {

    private Long id;
    /**
     * 问题结果分组id
     */
    private String resultGroupId;

    /**
     * 分组中图片的最早时间
     */
    private LocalDateTime earliestTime;

    /**
     * 分组中图片的最晚时间
     */
    private LocalDateTime latestTime;

    /**
     * 地址缩略图
     */
    private String addrImagePath;

    /**
     * 地址信息
     */
    private String addr;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 专题key
     */
    private String topicKey;

    /**
     * 问题等级
     */
    private Long topicLevelId;

    /**
     * 问题等级描述
     */
    private String topicLevelName;

    /**
     * 专题行业id
     * @deprecated 2.1.4，将在后续版本移除，使用industryType替换
     */
    @Deprecated
    private Long topicIndustryId;

    /**
     * 行业类型，取字典`GEOAI_INDUSTRY_TYPE`数据项项
     */
    private Integer industryType;

    /**
     * 行业描述
     */
    private String topicIndustryName;

    /**
     * 问题类型
     */
    private Long topicProblemId;

    /**
     * 问题类型描述
     */
    private String topicProblemName;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 任务名
     */
    private String taskName;

    /**
     * 任务类型(-1-app手动飞行；0-航点航线；1-全景采集；2-线状巡视；3-本地上传；4-动态规划；5-精细巡检；6-正射影像；7-倾斜影像)
     */
    private Integer taskType;

    /**
     * 变电站规划的次级类别0是本地任务，1是动态任务
     */
    private int subType;

    /**
     * 任务架次id
     */
    private Long missionId;

    /**
     * 任务架次名
     */
    private String missionName;

    /**
     * 顺序号，任务的第几个架次
     */
    private Integer missionSeqId;

    /**
     * 标签id
     */
    private Long tagId;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 基站id
     */
    private String baseNestId;

    /**
     * 基站名
     */
    private String nestName;

    /**
     * 单位id
     */
    private String orgCode;

    /**
     * 单位名
     */
    private String orgName;

    /**
     * 网格id
     */
    private String gridManageId;
}
