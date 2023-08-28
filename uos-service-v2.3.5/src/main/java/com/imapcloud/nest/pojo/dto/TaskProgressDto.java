package com.imapcloud.nest.pojo.dto;

import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务进度传输类
 *
 * @author wmin
 */
@Data
public class TaskProgressDto {

    /**
     * mission_records中的exec_id
     */
    private String execMissionId;

    private Integer taskId;
    /**
     * 任务名字，不要了
     */
    private String taskName;

    /**
     * 任务进度
     */
    private Double taskPercentage;

    /**
     * 任务飞行时长（多个架次飞行时间的总和）单位：second
     */
    private Long flyTime;

    /**
     * 当前执行的架次Id
     */
    private Integer currentMissionId;


    /**
     * 架次状态
     */
    private List<MissionProgressDto> missionProgressDtoList;

    /**
     * 机巢uuid
     */
    private String nestUuid;

    /**
     * 机巢Id
     */
    private String nestId;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 是否是批量任务,批量任务就是true,单个任务就是false
     */
    private Boolean multiTask;

    private Integer gainDataMode;

    private Integer gainVideo;

    private Extra extra;

    /**
     * 是否需要起飞录制
     */
    private Boolean takeOffRecord = false;

    private Boolean switchZoomCamera = false;

    public void setExtra(InitTaskProgressDtoParam param) {
        Extra extra = new Extra();
        BeanUtils.copyProperties(param, extra);
        this.extra = extra;
    }

    @Data
    @Accessors(chain = true)
    public static class Extra {
        private LocalDateTime startTime;
        private Integer flyIndex;
        private String missionUuid;
    }


}
