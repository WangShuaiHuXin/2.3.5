package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Classname DataAnalysisResultGroupPageOutDTO
 * @Description 数据分析问题统计结果分组页面OutDTO
 * @Date 2022/10/11 16:16
 * @Author Carnival
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DataAnalysisResultGroupOutDTO implements Serializable {

    @Data
    public static class ResultGroupOutDTO implements Serializable{
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
         * 发现次数
         */
        private Integer foundNums;

        private String thumImagePath;
    }

    @Data
    public static class ResultGroupExportOutDTO implements Serializable{

        private Long id;
        /**
         * 问题结果分组id
         */
        private String resultGroupId;

        /**
         * 发现次数
         */
        private Integer foundNums;

        /**
         * 分组中图片的最早时间
         */
        private LocalDateTime earliestTime;

        /**
         * 分组中图片的最晚时间
         */
        private LocalDateTime latestTime;

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
         * 地址链接
         */
        private String urlAddr;

        /**
         * 地址缩略图
         */
        private String addrImagePath;

        /**
         * 标签名
         */
        private String tagName;

        /**
         * 任务名
         */
        private String taskName;

        /**
         * 单位名
         */
        private String orgName;

        /**
         * 标注图片
         */
        private List<String> resultImages;

        /**
         * 经纬度
         */
        private String lngAndLat;

        /**
         * 行业描述
         */
        private String topicIndustryName;

        /**
         * 问题类型描述
         */
        private String topicProblemName;
    }

    @Data
    public static class ResultGroupExportResultOutDTO implements Serializable{

        /**
         * 照片集合
         */
        private List<ResultGroupExportOutDTO> resultList;

        /**
         * 最大照片数
         */
        private Integer maxImageNum;
    }

    @Data
    public static class ResultGroupPhotoDetailOutDTO implements Serializable {
        private Long centerBaseId;

        private Long centerDetailId;

        private Long photoId;

        private String photoName;

        private Integer photoState;

        private Integer pushState;

        private Long tagId;

        private Long taskId;

        private Long missionId;

        private Long missionRecordId;

        @Deprecated
        private Long nestId;

        /**
         * 基站ID
         */
        private String baseNestId;

        /**
         * @deprecated 2.0.0，使用orgCode替代
         */
        @Deprecated
        private Long orgId;

        /**
         * 单位编码
         */
        private String orgCode;

        private String thumImageMarkPath;

        private String imageMarkPath;

        private String thumImagePath;

        private String imagePath;

        private Integer srcDataType;

        private Integer picType;

        private BigDecimal longitude;

        private BigDecimal latitude;

        private Integer originalWidth;

        private Integer originalHeight;

        private LocalDateTime photoCreateTime;
    }
}
