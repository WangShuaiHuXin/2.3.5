package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("station_infrared_record_rectangle")
public class StationInfraredRecordRectangleEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * mission_photo的id
     */
    private Integer photoId;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

    private LocalDateTime createTime;


    /**
     * 最高温度
     */
    private Double maxTemperature;

    /**
     * 平均温度
     */
    private Double avgTemperature;

    /**
     * 最低温度
     */
    private Double minTemperature;
    /**
     * 坐标x
     */
    private String x;

    /**
     * 坐标y
     */
    private String y;

    /**
     * 坐标x1
     */
    private String x1;

    /**
     * 坐标y1
     */
    private String y1;

    /**
     * 标记类型-1：矩形，2：点
     */
    private Integer type;
    /**
     * 最高温的x坐标
     */
    private String maxTemperatureX;

    /**
     * 最高温的y坐标
     */
    private String maxTemperatureY;

    /**
     * 最低温的x坐标
     */
    private String minTemperatureX;

    /**
     * 最低温的y坐标
     */
    private String minTemperatureY;

    /**
     * 前端标识（LT）
     */
    @TableField(exist = false)
    private String key;

}
