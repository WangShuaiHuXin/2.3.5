package com.imapcloud.nest.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetPhotoParamDto {
    @ApiModelProperty(value = "基站id", position = 1, required = false, example = "")
    private String nestId;

    @ApiModelProperty(value = "任务架次记录", position = 2, required = false, example = "")
    private List<Integer> recordIdList;
}
