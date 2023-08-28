package com.imapcloud.nest.v2.web.vo.resp;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.converters.inputstream.InputStreamImageConverter;
import lombok.Data;
import lombok.ToString;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@ToString
public class DataAnalysisResultRespVO {

    private DataAnalysisResultRespVO() {

    }

    @Data
    public static class CollectSumRespVO {

        private Long problemSumNum;
        private List<ProblemNumInfoVO> problemNumInfoList;
    }

    @Data
    public static class ProblemNumInfoVO {

        /**
         * 问题等级
         */
        private String topicLevelId;

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
    public static class ProblemTrendRespVO {

        private String day;
        private Long problemNum;
    }

    @Data
    public static class ProblemRespVO implements Serializable {

        /**
         * 结果id
         */
        private String resultId;

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
        private String topicLevelId;

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
         * 标签id
         */
        private Long tagId;

        /**
         * 标签名
         */
        private String tagName;

        /**
         * 单位id
         */
        private Long orgId;

        /**
         * 单位名
         */
        private String orgName;

        private String createTime;

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
         * 变电站规划的次级类别0是本地任务，1是动态任务
         */
        private Integer subType;

        /**
         * 任务记录时间
         */
        private String missionRecordTime;

        private String nestId;
    }

    @Data
    public static class ProblemExportRespVO implements Serializable {

        /**
         * 标注原图
         */
        @ExcelProperty(value = "标注图片" , index = 0, converter = InputStreamImageConverter.class)
        private InputStream is;

        /**
         * 行业描述
         */
        @ExcelProperty(value = "行业" , index = 1)
        private String topicIndustryName;

        /**
         * 问题类型描述
         */
        @ExcelProperty(value = "问题" , index = 2)
        private String topicProblemName;

        @ExcelProperty(value = "时间" , index = 3)
        private String createTime;

        /**
         * 地址信息
         */
        @ExcelProperty(value = "地址" , index = 4)
        private String addr;

        /**
         * 地址信息
         */
        @ExcelProperty(value = "经纬度" , index = 5)
        private String ll;

        /**
         * 标注原图
         */
        @ExcelProperty(value = "地图" , index = 6, converter = InputStreamImageConverter.class)
        private InputStream addrImageIs;

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
    }
}
