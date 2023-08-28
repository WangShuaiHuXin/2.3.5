package com.imapcloud.nest.v2.service.dto.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Classname GridOutDTO
 * @Description 网格响应DTO
 * @Date 2022/12/7 15:11
 * @Author Carnival
 */


public class GridOutDTO {

    @Data
    public static class RegionOutDTO {

        private String gridRegionId;

        private String gridRegionCoor;

        private String gridRegion;

        private Integer sideLen;

        private String orgCode;

        private String orgName;

        List<GridManageOutDTO> gridManageList;
    }

    @Data
    public static class GridManageOutDTO {

        private Boolean hasRoute = false;

        private String gridManageId;

        private String gridRegionId;

        private Integer taskId;

        private Double west;

        private Double east;

        private Double south;

        private Double north;

        private Integer line;

        private Integer col;

        private Integer maxLine;

        private Integer maxCol;

        private Integer missionCount;

        private Integer problemCount;

        private List<String> orgCodes;
    }

    @Data
    public static class GridDataBatchDTO {

        private String gridManageId;

        private String orgCode;

        private List<GridDataOutDTO> gridDataOutDTOS;
    }

    @Data
    public static class GridDataOutDTO {

        private String gridManageId;

        private String gridDataId;

        private Double west;

        private Double east;

        private Double south;

        private Double north;

        private Integer line;

        private Integer col;

        private Integer seq;

        private String orgCode;
    }

    @Data
    public static class PhotoDTO {

        String gridManageId;

        Integer taskId;

        String missionName;

        Integer missionId;

        Integer missionRecordId;

        String taskName;

        Integer dataStatus;

        List<Photo> photoList;

        GridDataInfoDTO gridDataInfoDTO;
    }

    @Data
    public static class Photo implements Serializable {

        private Long id;
        /**
         * 图片名字
         */
        private String name;


        /**
         * 图片链接
         */
        private String photoUrl;

        /**
         * 图片大小
         */
        private Long photoSize;


        /**
         * 缩略图url
         */
        private String thumbnailUrl;

        /**
         * 架次id
         */
        private Integer missionId;

        /**
         * 记录表id
         */
        private Integer missionRecordsId;

        /**
         * 经度
         */
        private Double latitude;

        /**
         * 纬度
         */
        private Double longitude;

        /**
         * 海拔
         */
        private Double altitude;


        /**
         * 任务id
         */
        private Integer taskId;

        /**
         * 照片类型
         * 0 -> 可见光
         * 1 -> 红外光
         * 2 -> 可见红外合成
         */
        private Integer photoType;

        /**
         * 镜头类型 0-普通可见光镜头 1-广角可见光镜头 2-变焦可见光镜头 3-热红外镜头
         */
        private Integer lenType;


        /**
         * 照片拍摄时间
         */
        private LocalDateTime createTime;

        /**
         * 架次名称
         */
        private String missionName;

        /**
         * 任务名称
         */
        private String taskName;

        /**
         * 压缩标记图路径
         */
        private String thumImagePath;

        /**
         * 标记图路径
         */
        private String resultImagePath;

        /**
         * 标记图路径
         */
        private String centerDetailId;

        /**
         * 数据网格ID
         */
        private String gridDataId;

    }

    @Data
    public static class MissionRecordsDTO {

        private Integer missionRecordsId;

        private Integer missionId;

        private String name;

        private LocalDateTime createTime;

        private Integer isNewest;
    }

    @Data
    public static class InspectRecordDTO {

        private String gridInspectId;

        private String gridManageId;

        private String createTime;

        private Integer totalCount;

        private Integer executedCount;

        private Integer isNewest;

        private String orgCode;

        private String orgName;
    }

    @Data
    public static class MissionStatusDTO {

        private Integer taskId;

        private String gridInspectId;

        private Integer missionId;

        private String missionName;

        private Integer executeStatus;

    }

    @Data
    public static class GridStatisticsDTO {

        private Integer missionCount;

        private Integer problemCount;

        private Integer taskId;

        private String gridManageId;
    }

    @Data
    public static class GridManageHasDataDTO {

        @ApiModelProperty(value = "是否有数据", example = "10")
        private Boolean hasData;

        @ApiModelProperty(value = "管理网格ID", example = "10")
        private String gridManageId;
    }

    @Data
    public static class ProblemDTO {

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

        /**
         * 修改时间
         */
        private String modifiedTime;
    }

    @Data
    public static class OrgAndTaskOutDTO {

        private String orgCode;

        private String orgName;

        private Integer taskId;

        private String nestId;
    }

    @Data
    public static class OrgAndManageIdOutDTO {

        private String orgCode;

        private String gridManageId;
    }
}
