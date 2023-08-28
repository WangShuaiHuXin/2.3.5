package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.constant.MissionConstant;
import lombok.Data;

/**
 * 架次进度
 *
 * @author wmin
 */

@Data
public class MissionProgressDto {

    /**
     * mission_records中的exec_id
     */
    private String execMissionId;

    /**
     * 架次Id
     */
    private Integer missionId;

    private String missionUuid;
    /**
     * 架次名称
     */
    private String missionName;

    /**
     * 架次进度
     */
    private Double missionPercentage;

    /**
     * 架次状态
     * -1 - 未执行
     * 0 - 执行中
     * 1 - 执行完毕
     * 2 - 执行异常
     * 3 - 暂停
     * 4 - 终止
     */
    private Integer missionState;

    /**
     * 架次飞行时间,单位：second
     */
    private Long flyTime;

    /**
     * 架次的航点数
     */
    private Integer points;

    /**
     * 架次对应的航线Id
     */
    private Integer airLineId;

    /**
     * 获取任务的模式
     * 0 -> 不回传数据
     * 1 -> 延时回传数据
     * 2 -> 实时回传数据
     */
    private Integer gainDataMode = MissionConstant.MissionExecGainDataMode.NOT_BACK_UP;

    /**
     * 是否录制无人机图传视频（0：否， 1：是）
     */
    private Integer gainVideo = MissionConstant.MissionExecGainVideo.NOT_RECORD;
}
