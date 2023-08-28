package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 数据管理实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("image_data")
public class ImageDataEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 压缩包名称
     */
    private String zipName;

    /**
     * 压缩包存储路径
     */
    private String zipUrl;

    /**
     * 压缩包大小
     */
    private String zipSize;

    /**
     * 缩略图名称
     */
    private String thumbnailName;

    /**
     * 缩略图url
     */
    private String thumbnailUrl;

    /**
     * xml文件名称
     */
    private String xmlName;

    /**
     * xml文件url
     */
    private String xmlUrl;

    /**
     * 地区code数组
     */
    private String region;

//    /**
//     * 省code
//     */
//    private String province;
//    private Integer provinceCode;
//
//    /**
//     * 市code
//     */
//    private String city;
//    private Integer cityCode;
//
//    /**
//     * 区code
//     */
//    private String area;
//    private Integer areaCode;

    /**
     * 数据拍摄的时间
     */
    private LocalDateTime recordTime;

    /**
     * 单位id
     * @deprecated 2.0.0，使用orgCode字段替代
     */
    @Deprecated
    private Integer unitId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 单位名称
     */
    @TableField(exist = false)
    private String unitName;

    /**
     * 经度
     */
    private Double log;

    /**
     * 纬度
     */
    private Double lat;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;


}
