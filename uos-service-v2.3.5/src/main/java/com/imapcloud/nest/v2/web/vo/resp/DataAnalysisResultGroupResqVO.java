package com.imapcloud.nest.v2.web.vo.resp;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Classname DataAnalysisResultGroupResqVO
 * @Description 数据分析问题统计结果分组响应
 * @Date 2022/10/11 16:01
 * @Author Carnival
 */
@ApiModel("问题统计结果分组响应")
public class DataAnalysisResultGroupResqVO implements Serializable {

    @Data
    public static class ResultGroupRespVO implements Serializable {
        /**
         * 问题结果分组id
         */
        private String resultGroupId;

        /**
         * 分组中图片的最早时间
         */
        private String earliestTime;

        /**
         * 分组中图片的最晚时间
         */
        private String latestTime;

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
        private String topicProblemId;

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
        private String missionId;

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

        /**
         * 基站id(旧)
         */
        private String nestId;

        /**
         * 单位id(旧)
         */
        private String orgId;
    }

    @Data
    public static class ResultGroupExportRespVO {

        /**
         * 发现次数
         */
        @ExcelProperty(value = "发现次数" , index = 0)
        private Integer foundNums;

        /**
         * 分组中图片的最早时间
         */
        @ExcelProperty(value = "首次拍摄" , index = 1)
        private LocalDateTime earliestTime;

        /**
         * 分组中图片的最晚时间
         */
        @ExcelProperty(value = "最新拍摄" , index = 2)
        private LocalDateTime latestTime;

        /**
         * 地址信息
         */
        @ExcelProperty(value = "地址" , index = 3)
        private String addr;

        /**
         * 地址链接
         */
        @ExcelProperty(value = "位置链接" , index = 4)
        private String urlAddr;

        /**
         * 经纬度
         */
        @ExcelProperty(value = "经纬度" , index = 5)
        private String lngAndLat;

        /**
         * 地址缩略图
         */
        @ExcelProperty(value = "地图" , index = 6)
        private String addrImagePath;

        /**
         * 标签名
         */
        @ExcelProperty(value = "标签" , index = 7)
        private String tagName;

        /**
         * 任务名
         */
        @ExcelProperty(value = "任务" , index = 8)
        private String taskName;

        /**
         * 单位名
         */
        @ExcelProperty(value = "单位" , index = 9)
        private String orgName;

        /**
         * 标注图片
         */
        private List<String> resultImages;
    }

    @Data
    public static class TraceSpacetimeRespVO {

        private String time;

        private List<TraceInfoRespVO> list;
    }

    @Data
    public static class TraceInfoRespVO {

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

        private String baseNestId;

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

        private Integer isProblem;

        private Integer isResultGroup;

        private String yearMonth;
    }
}
