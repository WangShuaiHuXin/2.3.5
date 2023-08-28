package com.imapcloud.nest.v2.service.dto.in;

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
public class GridHistoryInDTO {


    private String gridDataId;

    private String startTime;

    private String endTime;
}
