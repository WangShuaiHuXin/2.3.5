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
 * 架次记录表
 * </p>
 *
 * @author wmin
 * @since 2020-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mission_records")
public class MissionRecordsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer missionId;

    /**
     * 机巢返回的执行Id,通过改值获取架次图片
     */
    private String execId;

    /**
     * 移动终端设备id
     */
    @Deprecated
    private Integer appId;

    private String baseAppId;
    /**
     * 执行的状态：0：未开始，1：进行中，2：执行完成，3：异常
     */
    private Integer status;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 图片数据传输状态
     * 0-未同步，1-同步到机巢，2-同步到平台，3-同步到机巢异常，4-同步到平台异常，5-停止同步
     */
    private Integer dataStatus;

    /**
     * 图片压缩包地址
     */
    private String dataPath;

    /**
     * 视频压缩包地址
     */
    private String videoPath;

    /**
     * 数据文件字节数
     */
    private Integer dataSize;

    /**
     * 飞行距离，米
     */
    private Double miles;

    /**
     * 飞行时长，秒
     */
    private Long seconds;

    /**
     * 航线执行到达第几个点
     */
    @TableField("reach_index")
    private Integer reachIndex;


    /**
     * 飞机照片是否备份到机巢，0 - 否，1 - 是
     */
    private Boolean backUpStatus;

    /**
     * 0-不备份，1-备份（飞机照片备份到机巢），2-同步&备份（飞机备份到机巢并且上传到图片服务器）
     */
    private Integer gainDataMode;

    /**
     * 是否保存录屏的字幕文件(0-不保存； 1-保存)
     */
    private Integer gainVideoData;

    /**
     * 是否录制无人机图传视频（0：否， 1：是）
     */
    private Integer gainVideo;

    /**
     * 架次飞行第几次
     */
    private Integer flyIndex;

    /**
     * 创建用户id
     */
    private Long createUserId;

    private LocalDateTime createTime;

    @TableField(exist = false)
    private String createTimeStr;

    private LocalDateTime modifyTime;

    /**
     * taskName，任务的名称
     */
    @TableField(exist = false)
    private String taskName;

    /**
     * taskId，任务的id
     */
    @TableField(exist = false)
    private Integer taskId;

    /**
     * sysTagName，标签名称
     */
    @TableField(exist = false)
    private String sysTagName;

    /**
     * tagId，标签id
     */
    @TableField(exist = false)
    private Integer sysTagId;

    /**
     * nestId，机巢id
     */
    @TableField(exist = false)
    private String nestId;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;


    /**
     * 0-未识别，1-已识别
     */
    private Integer recordStatus;

    /**
     * @deprecated 2.0.0，由orgCode字段替代
     */
    @Deprecated
    private String unitId;

    /**
     * 单位编码
      */
    private String orgCode;

    /**
     * uavWhich 无人机标识
     */
    private Integer uavWhich;

}
