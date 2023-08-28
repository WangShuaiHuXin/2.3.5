package com.imapcloud.nest.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 巡检成果管理_成果管理表
 *
 * @author: zxd
 * @create: 2020/5/27
 **/
@Data
@TableName("electric_result")
//@ApiModel(value = "成果管理表")
public class ElectricResultEntity implements Serializable {
    private  static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    //@ApiModelProperty(value = "成果管理 ID")
    private Integer id;

    /**
     * id
     */
    @TableField(exist=false)
    //@ApiModelProperty(value = "成果Ids")
    private Integer[] ids;

    /**
     * picPath
     */
    //@ApiModelProperty(value = "图片路径")
    private String picPath;

    /**
     * picName
     */
    //@ApiModelProperty(value = "图片名称")
    private String picName;

    /**
     * picModelName
     */
    //@ApiModelProperty(value = "图片模块名称")
    private String picModelName;

    /**
     * planId
     */
    //@ApiModelProperty(value = "计划id")
    private Integer planId;

    /**
     * uploadUser
     */
    //@ApiModelProperty(value = "上传人员")
    private String uploadUser;

    /**
     * lineId
     */
    //@ApiModelProperty(value = "线路id")
    private Integer lineId;

    /**
     * lineName
     */
    @TableField(exist=false)
    //@ApiModelProperty(value = "线路名")
    private String lineName;

    /**
     * towerId
     */
    //@ApiModelProperty(value = "杆塔id")
    private Integer towerId;

    /**
     * towerNum
     */
    @TableField(exist=false)
    //@ApiModelProperty(value = "杆塔编号")
    private String towerNum;

    /**
     * pilotId
     */
    //@ApiModelProperty(value = "飞手id")
    private Integer pilotId;

    /**
     * defectMark
     */
    //@ApiModelProperty(value = "缺陷标记(0:未标记;1:无缺陷；2：有缺陷)")
    private Integer defectMark;

    /**
     * uploadTime
     */
    //@ApiModelProperty(value = "图片上传时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date uploadTime;

    /**
     * longitude
     */
    //@ApiModelProperty(value = "经度（单位：度）")
    private BigDecimal longitude;

    /**
     * latitude
     */
    //@ApiModelProperty(value = "纬度（单位：度）")
    private BigDecimal latitude;

    /**
     * picThumbnailPath
     */
    //@ApiModelProperty(value = "缩略图路径")
    private String picThumbnailPath;

    //删除常量
    public static final Byte DELETE = 1;

    //未删除常量
    public static final Byte NOT_DELETE = 0;

    /**
     * 删除标记 1-删除 0-未删除 默认值为0
     */
    //@ApiModelProperty(value = "删除标记 1-删除 0-未删除 默认值为0")
    private Byte isDelete;


}
