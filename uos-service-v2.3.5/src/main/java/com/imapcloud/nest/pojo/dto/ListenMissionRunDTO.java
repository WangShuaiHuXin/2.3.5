package com.imapcloud.nest.pojo.dto;

import com.imapcloud.sdk.manager.ComponentManager;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ListenMissionRunDTO {
    private ComponentManager cm;
    private Integer missionId;
    private String missionUuid;
    private Integer gainDataMode;
    private Integer gainVideo;
    private String nestUuid;
    private String execId;
    private Integer flyTimes;
    private LocalDateTime startTime;
    /**
     * 是否是变焦航线
     */
    private Boolean switchZoomCamera;

    /**
     * 是否起飞录制
     */
    private Boolean takeOffRecord;

    private Integer taskId;

    private Integer taskType;

    private String missionName;

    /**
     * 无人机标识
     */
    private Integer uavWhich;

    /**
     * 网格化巡检记录ID
     */
    private String gridInspectId;

    /**
     * 账号ID
     * @since 2.2.5
     */
    private String accountId;
}
