package com.imapcloud.nest.v2.web.vo.req;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Classname GridReqVO
 * @Description 区域网格ReqVO
 * @Date 2022/12/6 19:55
 * @Author Carnival
 */
@ApiModel("网格化请求VO")
@ToString
public class GridReqVO {
    @ApiModel("区域网格VO")
    @Data
    public static class RegionReqVO {


        @ApiModelProperty(value = "储存上传的区域坐标信息",  required = true, example = "[2993.23,841.248]")
        @NotBlank(message = "{geoai_uos_grid_service_10}")
        String gridRegionCoor;

        @ApiModelProperty(value = "生成的网格边界", required = true, example = "[2993.23,841.248]")
        @NotBlank(message = "{geoai_uos_grid_service_11}")
        String gridRegion;

        @ApiModelProperty(value = "网格边长", required = true, example = "10")
        @NotBlank(message = "网格边长不能为空")
        Integer sideLen;

        @ApiModelProperty(value = "单位", required = true, example = "000")
        @NotBlank(message = "单位不能为空")
        String orgCode;

        @ApiModelProperty(value = "管理网格列表")
        List<GridManageReqVO> gridManageList;
    }

    @ApiModel("管理网格VO")
    @Data
    public static class GridManageReqVO {

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

    @ApiModel("管理网格与单位关联VO")
    @Data
    public static class GridManageOrgCodeVO {

        @ApiModelProperty(value = "管理网格ID", required = true, example = "10")
        private String gridManageId;

        @ApiModelProperty(value = "单位ID", required = true, example = "10")
        private String orgCode;
    }

    @ApiModel("管理网格与单位关联VO")
    @Data
    public static class GridCancelRelReqVO {

        @ApiModelProperty(value = "管理网格ID", required = true, example = "1402211972623324")
        private String gridManageId;

        @ApiModelProperty(value = "原单位", required = true, example = "000")
        private String orgCode;
    }
}
