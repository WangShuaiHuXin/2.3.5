package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Classname UosRegionQueryRespVO
 * @Description 区域管理详细信息类
 * @Date 2022/8/11 18:30
 * @Author Carnival
 */
@Data
public class UosRegionQueryRespVO implements Serializable {

    @ApiModelProperty(value = "区域ID", position = 1,  example = "2244333368909")
    private String regionId;

    @ApiModelProperty(value = "区域名称", position = 2,  example = "北京")
    private String regionName;

    @ApiModelProperty(value = "描述", position = 3,  example = "首都")
    private String description;

    @ApiModelProperty(value = "创建时间", position = 7,  example = "2022-08-02")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新时间", position = 8,  example = "2022-08-02")
    private LocalDateTime modifiedTime;

}
