package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 视屏抽帧的图片
 * </p>
 *
 * @author hc
 * @since 2021-06-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mission_video_photo")
public class MissionVideoPhotoEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 图片名字
     */
    private String name;

    /**
     * 视屏抽取时间
     */
    private LocalDateTime extractTime;

    /**
     * 架次id
     */
    private Integer missionId;

    /**
     * 架次记录表的id
     */
    private Integer missionRecordsId;

    /**
     * 几秒一帧
     */
    private Integer seconds;

    /**
     * 状态(0-终止；1-正常)
     */
    private Boolean state;

    /**
     * 图片链接
     */
    private String photoUrl;

    /**
     * 缩略图url
     */
    private String thumbnailUrl;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

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
     * 间隔时间
     */
    private Integer spaceTime;


    /**
     * @deprecated 2.0.0，使用orgCode字段替代
     */
    @Deprecated
    private String unitId;

    private String orgCode;

    private Integer tagVersion;
}
