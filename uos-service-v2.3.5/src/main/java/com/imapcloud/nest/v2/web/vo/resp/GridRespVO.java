package com.imapcloud.nest.v2.web.vo.resp;

import com.imapcloud.nest.v2.service.dto.out.GridDataInfoDTO;
import com.imapcloud.nest.v2.service.dto.out.GridOutDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Classname GridRespVO
 * @Description 区域网格RespVO
 * @Date 2022/12/7 14:08
 * @Author Carnival
 */
@ApiModel("网格化响应VO")
@ToString
public class GridRespVO {

    @ApiModel("网格任务响应VO")
    @Data
    public static class MissionRecordVO implements Serializable {

        @ApiModelProperty(value = "网格任务ID",  example = "4535565243434")
        String gridMissionId;

        @ApiModelProperty(value = "任务名称",  example = "架次1")
        String missionName;

        @ApiModelProperty(value = "任务ID",  example = "22")
        String missionId;

        @ApiModelProperty(value = "基站名称",  example = "G900")
        String baseNestName;

        @ApiModelProperty(value = "任务ID",  example = "22")
        String baseNestId;

        @ApiModelProperty(value = "创建时间",  example = "2022-12-7")
        LocalDateTime createTime;

        @ApiModelProperty(value = "是否同步",  example = "0")
        Integer isState;
    }

    @ApiModel("网格航点缩略图VO")
    @Data
    public static class PhotoVO implements Serializable {

        @ApiModelProperty(value = "数据网格ID",  example = "5676654345567")
        String gridDataId;

        @ApiModelProperty(value = "照片缩略图Url",  example = "/data/origin/95a5b615-4859-455e-b655-f13a60fb3d9fDJI_0001.jpg")
        String thumbnailUrl;

    }

    @ApiModel("区域网格VO")
    @Data
    public static class RegionRespVO {

        @ApiModelProperty(value = "区域网格ID",  required = true, example = "153554595262")
        String gridRegionId;

        @ApiModelProperty(value = "储存上传的区域坐标信息",  required = true, example = "[2993.23,841.248]")
        String gridRegionCoor;

        @ApiModelProperty(value = "生成的网格边界", required = true, example = "[2993.23,841.248]")
        String gridRegion;

        @ApiModelProperty(value = "网格边长", required = true, example = "10")
        String sideLen;

        @ApiModelProperty(value = "单位编码", required = true, example = "000")
        String orgCode;

        @ApiModelProperty(value = "单位名称", required = true, example = "000")
        String orgName;;

        @ApiModelProperty(value = "管理网格列表")
        List<GridRespVO.GridManageRespVO> gridManageList;
    }

    @ApiModel("管理网格VO")
    @Data
    public static class GridManageRespVO {

        @ApiModelProperty(value = "是否有航线", required = true, example = "8.467")
        private Boolean hasRoute;

        @ApiModelProperty(value = "管理网格ID", required = true, example = "8.467")
        private String gridManageId;

        @ApiModelProperty(value = "区域网格ID", required = true, example = "8.467")
        private String gridRegionId;

        @ApiModelProperty(value = "航线ID", required = true, example = "8.467")
        private Integer taskId;

        @ApiModelProperty(value = "西边", required = true, example = "8.467")
        Double west;

        @ApiModelProperty(value = "南边", required = true, example = "9.23")
        Double south;

        @ApiModelProperty(value = "东边", required = true, example = "10.46")
        Double east;

        @ApiModelProperty(value = "北边", required = true, example = "11.44")
        Double north;

        @ApiModelProperty(value = "行号", required = true, example = "10")
        Integer line;

        @ApiModelProperty(value = "列号", required = true, example = "10")
        Integer col;

        @ApiModelProperty(value = "最大行号",  example = "10")
        private Integer maxLine;

        @ApiModelProperty(value = "最大列号",  example = "10")
        private Integer maxCol;

        @ApiModelProperty(value = "巡检次数",  example = "10")
        private Integer missionCount;

        @ApiModelProperty(value = "问题次数",  example = "10")
        private Integer problemCount;

        @ApiModelProperty(value = "所属单位",  example = "10")
        private List<String> orgCodes;
    }

    @ApiModel("数据网格VO")
    @Data
    public static class GridDateRespVO {

        @ApiModelProperty(value = "管理网格ID", required = true, example = "112555555555")
        String gridManageId;

        @ApiModelProperty(value = "数据网格ID", required = true, example = "112555555555")
        String gridDataId;

        @ApiModelProperty(value = "西边", required = true, example = "8.467")
        Double west;

        @ApiModelProperty(value = "南边", required = true, example = "9.23")
        Double south;

        @ApiModelProperty(value = "东边", required = true, example = "10.46")
        Double east;

        @ApiModelProperty(value = "北边", required = true, example = "11.44")
        Double north;

        @ApiModelProperty(value = "行号", required = true, example = "10")
        Integer line;

        @ApiModelProperty(value = "列号", required = true, example = "10")
        Integer col;
    }

    @Data
    public static class GridPhotoVO {

        @ApiModelProperty(value = "管理网格ID", required = true, example = "10")
        String gridManageId;

        @ApiModelProperty(value = "航线ID", required = true, example = "10")
        Integer taskId;

        @ApiModelProperty(value = "任务名称", required = true, example = "10")
        String taskName;

        @ApiModelProperty(value = "架次名称", required = true, example = "10")
        String missionName;

        @ApiModelProperty(value = "架次ID", required = true, example = "10")
        Integer missionId;

        @ApiModelProperty(value = "架次记录ID", required = true, example = "10")
        Integer missionRecordId;

        @ApiModelProperty(value = "照片列表", required = true, example = "10")
        List<GridOutDTO.Photo> photoList;

        @ApiModelProperty(value = "数据状态", required = true, example = "10")
        Integer dataStatus;

        @ApiModelProperty(value = "第一个数据网格信息", required = true, notes = "前端用来定位画图，内容包括东西南北", example = "10")
        GridDataInfoDTO gridDataInfoDTO;
    }


    @Data
    public static class MissionRecordsVO {

        @ApiModelProperty(value = "架次记录ID", required = true, example = "10")
        private Long missionRecordsId;

        @ApiModelProperty(value = "架次ID", required = true, example = "10")
        private Long missionId;

        @ApiModelProperty(value = "架次名称", required = true, example = "10")
        private String name;

        @ApiModelProperty(value = "创建时间", required = true, example = "10")
        private LocalDateTime createTime;

        @ApiModelProperty(value = "是否最新的数据", required = true, example = "10")
        private Integer isNewest;
    }

    @Data
    public static class InspectRecordVO {

        @ApiModelProperty(value = "巡检记录Id", required = true, example = "10")
        private String gridInspectId;

        @ApiModelProperty(value = "巡检记录创建时间", required = true, example = "10")
        private String createTime;

        @ApiModelProperty(value = "架次总数", required = true, example = "10")
        private Integer totalCount;

        @ApiModelProperty(value = "已执行次数", required = true, example = "10")
        private Integer executedCount;

        @ApiModelProperty(value = "是否最新巡检记录", required = true, example = "10")
        private Integer isNewest;

        @ApiModelProperty(value = "管理网格Id", required = true, example = "10")
        private String gridManageId;

        @ApiModelProperty(value = "单位编码", required = true, example = "000")
        private String orgCode;

        @ApiModelProperty(value = "单位名称", required = true, example = "中科云图")
        private String orgName;
    }

    @Data
    public static class MissionStatusVO {

        @ApiModelProperty(value = "巡检记录Id", required = true, example = "10")
        private String gridInspectId;

        @ApiModelProperty(value = "架次Id", required = true, example = "10")
        private Integer missionId;

        @ApiModelProperty(value = "架次名称", required = true, example = "10")
        private String missionName;

        @ApiModelProperty(value = "执行状态", required = true, example = "10")
        private Integer executeStatus;

        @ApiModelProperty(value = "航线任务ID", required = true, example = "10")
        private Integer taskId;

    }

    @Data
    public static class MissionRecordsStatusVO {

        @ApiModelProperty(value = "架次记录ID", required = true, example = "10")
        private Long missionRecordsId;

        @ApiModelProperty(value = "是否已执行", required = true, example = "10")
        private Integer isExecute;
    }



    @Data
    public static class GridStatisticsVO {

        @ApiModelProperty(value = "巡检次数",  example = "10")
        private Integer missionCount;

        @ApiModelProperty(value = "发现线索",  example = "10")
        private Integer problemCount;

        @ApiModelProperty(value = "航线ID",  example = "10")
        private Integer taskId;

        @ApiModelProperty(value = "管理网格ID",  example = "10")
        private String gridManageId;
    }

    @Data
    public static class GridManageHasDataVO {

        @ApiModelProperty(value = "是否有数据",  example = "10")
        private Boolean hasData;

        @ApiModelProperty(value = "管理网格ID",  example = "10")
        private String gridManageId;
    }

    @Data
    public static class GridDataBatchVO {

        @ApiModelProperty(value = "管理网格ID",  example = "12534525423333")
        private String gridManageId;

        @ApiModelProperty(value = "单位",  example = "000")
        private String orgCode;

        @ApiModelProperty(value = "数据网格")
        private List<GridOutDTO.GridDataOutDTO> gridDataOutDTOS;
    }

    @Data
    public static class GridOrgParseVO {

        @ApiModelProperty(value = "单位名称",  example = "10")
        private String orgName;

        @ApiModelProperty(value = "网格单位分配矢量",  example = "10")
        private String gridOrgVector;
    }

    @Data
    public static class OrgAndTaskRespVO {

        @ApiModelProperty(value = "单位编码", position = 1,  example = "000")
        private String orgCode;

        @ApiModelProperty(value = "单位名称", position = 2,  example = "中科云图")
        private String orgName;

        @ApiModelProperty(value = "航线ID", position = 3,  example = "15320")
        private Integer taskId;

        @ApiModelProperty(value = "基站ID", position = 5,  example = "1452255424145")
        private String nestId;
    }
}
