package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据分析结果
 *
 * @author boluo
 * @date 2022-07-18
 */
@ToString
public class DataAnalysisResultOutDTO {
    private DataAnalysisResultOutDTO() {}

    @Data
    public static class ProblemTrendOut {

        private String day;
        private Long problemNum;
    }

    @Data
    public static class CollectSumOut {

        private Long problemSumNum;
        private List<ProblemNumInfo> problemNumInfoList;
    }

    @Data
    public static class ProblemNumInfo {
        /**
         * 问题等级
         */
        private Long topicLevelId;

        /**
         * 问题等级描述
         */
        private String topicLevelName;

        /**
         * 问题数量
         */
        private Long problemNum;
    }

    @Data
    public static class ProblemOut implements Serializable {

        /**
         * 结果id
         */
        private Long resultId;

        /**
         * 图片id
         */
        private Long photoId;

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
         * 任务类型(-1-APP手动飞行；0-航点航线；1-全景采集；2-线状巡视；3-本地上传；4-动态规划；5-精细巡检；6-正射影像；7-倾斜影像)
         */
        private Integer taskType;

        /**
         * 顺序号，任务的第几个架次
         */
        private Integer missionSeqId;

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
         * 行业
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
         * 任务架次id
         */
        private Long missionId;

        /**
         * 任务架次名
         */
        private String missionName;

        /**
         * 任务架次记录id
         */
        private Long missionRecordsId;

        /**
         * 任务架次记录序号
         */
        private String missionRecordsName;

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
         */
        private String orgId;

        /**
         * 单位名
         */
        private String orgName;

        private LocalDateTime createdTime;

        /**
         * 变电站规划的次级类别0是本地任务，1是动态任务
         */
        private Integer subType;

        /**
         * 任务记录时间
         */
        private LocalDateTime missionRecordsTime;
    }
}
