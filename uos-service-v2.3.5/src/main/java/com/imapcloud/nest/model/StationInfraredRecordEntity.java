package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 变电站的设备出现的红外测温记录
 * </p>
 *
 * @author hc
 * @since 2020-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("station_infrared_record")
public class StationInfraredRecordEntity implements Serializable {

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

    private LocalDateTime modifyTime;

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
     * 可见光地址
     */
    private String sunUrl;

    /**
     * 融合地址
     */
    private String pipUrl;


    /**
     * 识别地址
     */
    private String recordUrl;

    /**
     * 1有问题0没问题
     */
    private Integer flag;

    /**
     * tiff图地址
     */
    private String tiffUrl;

    @TableField(exist = false)
    private Integer tagId;
    @TableField(exist = false)
    private Double lat;
    @TableField(exist = false)
    private Double lng;
}
