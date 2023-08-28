package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.*;

/**
 * <p>
 *
 * </p>
 *
 * @author wmin
 * @since 2020-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mission_video")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MissionVideoEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 视频名字
     */
    private String name;

    /**
     * 视频别名
     */
    private String alias;

    /**
     * 视频链接
     */
    private String videoUrl;

    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private Integer srtId;

    /**
     * 伴随文件json地址
     */
    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private String srtUrl;

    /**
     * 伴随文件地址
     */
    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private String srtFileUrl;

    @TableField(exist = false)
    private String srtJson;

    /**
     * 视频大小
     */
    private Long videoSize;

    /**
     * 视频缩略图
     */
    private String videoThumbnail;

    /**
     * 视频类型（1：录像的视频；2：同步的原视频）
     */
    private Integer type;
    /**
     * 录屏是否结束（0：未结束；1：已结束）
     */
    private Integer recordStatus;
    /**
     * 架次id
     */
    private Integer missionId;

    /**
     * 架次记录表的id
     */
    private Integer missionRecordsId;
    /**
     * 任务执行的execId
     */
    private String execId;

    /**
     * 图片在机巢的id
     */
    private String fileId;

    /**
     * 图片在机巢的名称
     */
    private String fileName;

    /**
     * 图片在机巢的文件类型
     */
    private Integer mediaType;

    /**
     * 图片在机巢的文件创建时间
     */
    private LocalDateTime timeCreated;

    /**
     * 图片是否已经下载到机巢(0：否； 1：是)
     */
    private Integer downloaded;

    /**
     * @since 2.3.2，录像任务ID
     */
    private String recordTaskId;

    /**
     * @since 2.3.2，录像视频索引
     */
    private Integer recordIndex;

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

    private Boolean physicalDeleted;

    private LocalDateTime mediaCreateTime;

    private Integer taskId;

    @TableField(exist = false)
    private Double lat;
    @TableField(exist = false)
    private Double lng;
    @TableField(exist = false)
    private String taskName;

    /**
     * @deprecated 2.0.0，使用orgCode字段替代
     */
    @Deprecated
    private String unitId;

    private String orgCode;

    private Integer tagVersion;
}
