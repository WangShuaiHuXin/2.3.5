package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataInterestPointReqVO.java
 * @Description DataInterestPointReqVO
 * @createTime 2022年09月16日 11:30:00
 */
@Data
public class DataInterestPointReqVO{


    /**
     * 兴趣点名称
     */
    @ApiModelProperty(value = "兴趣点名称",required = true, position = 1, example = "中科云图")
    @NotBlank(message = "兴趣点名称不能为空")
    @Length(max = 30, message = "兴趣点名称限30个字符")
    private String pointName;

    /**
     * 兴趣点高度
     */
    @ApiModelProperty(value = "兴趣点高度",required = true,  position = 1, example = "500.0")
    @NotBlank(message = "兴趣点高度不能为空")
    private BigDecimal pointHeight;

    /**
     * 兴趣点经度
     */
    @ApiModelProperty(value = "兴趣点经度",required = true,  position = 1, example = "180.00")
    @NotBlank(message = "兴趣点经度不能为空")
    private BigDecimal pointLongitude;

    /**
     * 兴趣点纬度
     */
    @ApiModelProperty(value = "兴趣点纬度",required = true,  position = 1, example = "90.00")
    @NotBlank(message = "兴趣点纬度不能为空")
    private BigDecimal pointLatitude;

    /**
     * 兴趣点类型
     */
    @ApiModelProperty(value = "兴趣点类型",required = true,  position = 1, example = "0",
            notes = "1、单位 2、工地 3、建筑 4、景点 5、河湖 6、道路")
    @NotBlank(message = "兴趣点类型不能为空")
    private Integer pointType;

    /**
     * 全景点地址信息
     */
    @ApiModelProperty(value = "全景点地址信息",required = true,  position = 1, example = "广东省越秀区")
    @NotBlank(message = "全景点地址信息不能为空")
    @Length(max = 200, message = "全景点地址信息限200个字符")
    private String address;

    /**
     * 简介
     */
    @ApiModelProperty(value = "简介", position = 1, example = "")
    @Length(max = 200, message = "全景点地址信息限200个字符")
    private String brief;

    /**
     * 单位编码
     */
    @ApiModelProperty(value = "单位编码", position = 1, example = "000")
    private String orgCode;

    /**
     * 基站id
     */
    @ApiModelProperty(value = "基站id", position = 1, example = "1563200098241614")
    private String baseNestId;

    /**
     * 兴趣点地图范围
     */
    @ApiModelProperty(value = "兴趣点地图显示范围",required = true,  position = 1, example = "0")
    @NotBlank(message = "兴趣点地图显示范围信息不能为空")
    private Integer mapDistance;

    /**
     * 兴趣的全景范围
     */
    @ApiModelProperty(value = "兴趣点全景显示范围",required = true,  position = 1, example = "0")
    @NotBlank(message = "兴趣点全景显示范围信息不能为空")
    private Integer panoramaDistance;

    /**
     * 标签
     */
    @ApiModelProperty(value = "兴趣点全景显示范围",required = true,  position = 1, example = "0")
    @NotBlank(message = "标签不能为空")
    private String tagId;
}