package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 违建点信息表
 * </p>
 *
 * @author zheng
 * @since 2021-03-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("illegal_point")
public class IllegalPointEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 点云文件id1
     */
    private Integer beforeFileId;

    /**
     * 点云文件id2
     */
    private Integer afterFileId;

    /**
     * 违建点名称
     */
    private String name;

    /**
     * 照片id
     */
    private Long photoId;

    /**
     * 违建点图片路径
     */
    private String photoUrl;

    /**
     * 违建点图片缩略图路径
     */
    private String thumbnailUrl;

    /**
     * 标记照片路径
     */
    private String markPhotoUrl;

    /**
     * 违建点面积
     */
    private Double area;

    /**
     * 经度
     */
    private Double latitude;

    /**
     * 纬度
     */
    private Double longitude;

    /**
     * 高度
     */
    private Double height;

    /**
     * 备注
     */
    private String note;

    /**
     * 智能分析的坐标信息
     */
    private String coordinates;

    /**
     * 正射列点坐标信息
     */
    private String pointInfo;

    /**
     * 类型（1-手动标记；2-智能分析）
     */
    private Integer type;

    /**
     * 来源（2-正射； 3-点云；）
     */
    private Integer source;

    /**
     * 问题来源（0-缺陷识别；1-表记读数；2-红外测温；3-排污检测；4-河道巡查；5-管道巡查；6-水库巡查；7-城市巡查；8-违建识别；
     * 9-非法摆摊；10-公安巡查；11-违法取证；12-治安巡逻；13-应急巡查；14-事故勘察；15-应急指挥；16-国土改造；17-违章识别；
     * 18-国土取证；19-环保巡查；20-气体监测；21-排污取证；22-交通巡查；23-故事取证；24-智慧工地）
     */
    private Integer problemSource;
//    /**
//     * 问题来源（1-违章建筑；2-围垦侵占）
//     */
//    private Integer problemType;
//    /**
//     * 地址（河长通用）
//     */
//    private String address;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    @TableLogic
    private Boolean deleted;

    @TableField(exist = false)
    private MultipartFile multipartFile;

    /**
     * 点云，正射的文件名称
     */
    @TableField(exist = false)
    private String FileName;
    /**
     * 点云，正射文件时间
     */
    @TableField(exist = false)
    private String fileDateTime;
    @TableField(exist = false)
    private LocalDateTime recordTime;
    @TableField(exist = false)
    private List<DefectInfoEntity> defectInfoEntityList;
}
