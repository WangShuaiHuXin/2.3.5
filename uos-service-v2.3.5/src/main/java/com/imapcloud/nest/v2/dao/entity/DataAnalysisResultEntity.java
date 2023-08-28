package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_analysis_result")
public class DataAnalysisResultEntity extends GenericEntity {

    /**
     * 结果id
     */
    private Long resultId;

    /**
     * 图片id
     */
    private Long photoId;

    /**
     * 结果分组ID
     */
    private String resultGroupId;
    /**
     * 标注id
     */
    private Long markId;

    /**
     * 是否ai标注，0-否，1-是
     */
    private Boolean aiMark;

    /**
     * 标注缩略图
     */
    private String thumImagePath;

    /**
     * 地址缩略图
     */
    private String addrImagePath;

    /**
     * 标注原图
     */
    private String resultImagePath;

    /**
     * 图片来源
     */
    private String imagePath;

    /**
     * 地址信息
     */
    private String addr;

    /**
     * 照片创建时间
     */
    private LocalDateTime photoCreateTime;

    /**
     * 数据来源（0-现场取证、1-照片、2-视频）
     */
    private Integer srcDataType;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

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
     * 任务类型(-1-APP手动飞行；0-航点航线；1-全景采集；2-线状巡视；3-本地上传；4-动态规划；5-精细巡检；6-正射影像；7-倾斜影像)
     */
    private Integer taskType;

    /**
     * 变电站规划的次级类别0是本地任务，1是动态任务
     */
    private Integer subType;

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
     * 任务架次记录id
     */
    private Long missionRecordsId;

    /**
     * 任务架次记录序号
     */
    private String missionRecordsName;

    /**
     * 任务记录时间
     */
    private LocalDateTime missionRecordsTime;

    /**
     * 标签id
     */
    private Long tagId;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 基站ID
     */
    private String baseNestId;

    /**
     * 基站名
     */
    private String nestName;

    /**
     * 单位id
     *
     * @deprecated 2.0.0，使用orgCode替代
     */
    @Deprecated
    private Long orgId;

    /**
     * 单位编码
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
