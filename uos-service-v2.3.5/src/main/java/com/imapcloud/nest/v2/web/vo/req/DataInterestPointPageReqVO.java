package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname DataInterestPointPageReqVO
 * @Description 全景兴趣点分页VO
 * @Date 2022/9/26 14:33
 * @Author Carnival
 */
@Data
public class DataInterestPointPageReqVO extends PageInfo implements Serializable {

    /**
     * 兴趣点名称
     */
    @ApiModelProperty(value = "兴趣点名称", position = 1, example = "兴趣点名称")
    private String pointName;

    /**
     * 兴趣点类型
     */
    @ApiModelProperty(value = "兴趣点类型", position = 1, example = "1")
    private Integer pointType;

    /**
     * 单位
     */
    @ApiModelProperty(value = "单位", position = 1, example = "中科云图")
    private String orgCode;

    /**
     * 分组
     */
    @ApiModelProperty(value = "分组", position = 1, example = "中科云图")
    private String tagId;
}
