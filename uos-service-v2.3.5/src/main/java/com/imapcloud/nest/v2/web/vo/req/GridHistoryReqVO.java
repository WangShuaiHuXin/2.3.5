package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Classname GridHistoryReqVO
 * @Description 数据网格获取历史记录
 * @Date 2022/12/21 14:56
 * @Author Carnival
 */
@Data
@ApiModel("数据网格获取历史记录VO")
public class GridHistoryReqVO {

    @ApiModelProperty(value = "储存上传的区域坐标信息",required = true, example = "1444445253535")
    @NotBlank(message = "数据网格ID不能为空")
    private String gridDataId;

    @ApiModelProperty(value = "开始时间", required = true, example = "2022-12-21")
    private String startTime;

    @ApiModelProperty(value = "结束时间", required = true, example = "2022-12-21")
    private String endTime;
}
