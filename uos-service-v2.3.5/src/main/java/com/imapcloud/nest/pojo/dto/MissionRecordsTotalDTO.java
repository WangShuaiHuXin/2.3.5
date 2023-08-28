package com.imapcloud.nest.pojo.dto;

import lombok.Data;

/**
 * 首页-巡检记录DTO
 *
 * @author: zhengxd
 * @create: 2021/1/18
 **/
@Data
public class MissionRecordsTotalDTO {
    /**
     * 标签
     */
    private Integer tagId;
    private String tagName;

    /**
     * 任务
     */
    private Integer taskId;
    private String taskName;
    private Integer taskType;

    /**
     * 架次记录
     */
    private Integer recordId;
    private String createTime;

    /**
     * 机巢
     */
    private String nestId;
    private String nestName;
    private Integer nestType;

    /**
     * 移动终端
     */
    private String appId;
    private String appName;

    /**
     * TOP5的视频数量
     */
    private Integer photoCount;

    /**
     * TOP5的视频数量
     */
    private Integer videoCount;

    private Integer seqId;

    /**
     * 账号ID
     * @since 2.2.5
     */
    private Long createUserId;
    /**
     * 账号真实姓名
     * @since 2.2.5
     */
    private String realName;

}
