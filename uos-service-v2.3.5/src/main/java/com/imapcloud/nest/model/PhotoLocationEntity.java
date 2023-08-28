package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhengxd
 * @since 2021-06-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("photo_location")
public class PhotoLocationEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 照片问题位置表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 照片id
     */
    private Long photoId;

    /**
     * 位置信息
     */
    private String locationInfo;
    /**
     * 位置图片名称
     */
    private String locationName;

    /**
     * 位置图片路劲
     */
    private String locationUrl;

    /**
     * 备注
     */
    private String note;

    private LocalDateTime modifyTime;

    private LocalDateTime createTime;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

    /**
     * 纬度
     */
    @TableField(exist = false)
    private Double lat;
    /**
     * 经度
     */
    @TableField(exist = false)
    private Double lng;
    /**
     * 照片名称
     */
    @TableField(exist = false)
    private String photoName;
    /**
     * 照片路劲
     */
    @TableField(exist = false)
    private String photoUrl;
    @TableField(exist = false)
    private String thumbnailUrl;
    /**
     * 问题名称
     */
    @TableField(exist = false)
    private String defectName;

    /**
     * 相近图片
     */
    @TableField(exist = false)
    private Integer clearId;

    /**
     * 相近图片
     */
    @TableField(exist = false)
    private MissionPhotoEntity clearPhoto;

    /**
     * 相近图片
     */
    @TableField(exist = false)
    private String taskName;

    @TableField(exist = false)
    private String problemUrl;

}
