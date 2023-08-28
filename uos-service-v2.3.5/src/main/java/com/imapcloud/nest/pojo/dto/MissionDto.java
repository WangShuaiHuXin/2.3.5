package com.imapcloud.nest.pojo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wmin
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MissionDto {
    private Integer missionId;

    /**
     * 名称
     */
    private String name;

    /**
     * nest的uuid
     */
    private String uuid;

    /**
     * 移动终端id
     */
    private String appId;

    /**
     * 顺序号，任务的第几个架次
     */
    private Integer seqId;

    /**
     * 航线ID
     */
    private Integer airLineId;

    /**
     * 任务ID
     */
    private Integer taskId;

    /**
     * 航次参数Id
     */
    private Integer missionParamId;

    /**
     * 创建用户id
     * @since 2.2.5
     */
    private Long createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean   deleted;

    /**
     *  任务名称
     */
    private String taskName;

    /**
     * 任务架次本次飞行的id(missionRecordId)
     */
    Integer id;
    /**
     * 任务架次飞行的次数
     */
    private Integer flyIndex;

    /**
     * 该架次本次飞行的完成时间
     */
    private LocalDateTime endTime;
    /**
     * 该架次本次飞行的开始时间
     */
    private LocalDateTime startTime;

    /**
     * 图片数据传输状态
     * 0-未同步，1-同步到机巢，2-同步到平台，3-同步到机巢异常，4-同步到平台异常，5-停止同步
     */
    private Integer dataStatus;
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
     * 执行的状态：0：未开始，1：进行中，2：执行完成，3：异常
     */
    private Integer status;
    /**
     * 该架次图片数量
     */
    private Integer photoCount;
    /**
     * 航线拍视频的数量
     */
    private Integer videoCount;
    /**
     * 标签id，名称
     */
    private Integer tagId;
    private String tagName;

    private Integer highVideo;

    private Integer recordVideo;

    private boolean hasAutoData;
    /**
     * 无人机标识
     */
    private Integer uavWhich;

    /**
     * 用户姓名
     */
    private String realName;

    /**
     * 异常信息，如果    同步正常 dataStatus！=4  同步异常 dataStatus==4
     */
    private List<ErrInfo> errInfo;
    @Data
    public static  class ErrInfo{
            public String code;
            private String desc;
    }
}
