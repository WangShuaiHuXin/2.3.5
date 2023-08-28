package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.model.MissionPhotoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author wmin
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StationPlaningMissionDto {
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
     * 任务ID
     */
    private Integer taskId;

    /**
     * 任务ID
     */
    private Integer tagId;

    /**
     *  标签名字
     */
    private String sysTagName;

    /**
     *  缺陷数目
     */
    private Integer defectCount;

    /**
     * 该架次本次飞行的完成时间
     */
    private LocalDateTime endTime;
    /**
     * 该架次本次飞行的开始时间
     */
    private LocalDateTime startTime;

    private List<MissionPhotoListWrapDto> missionPhotoListWrapDto;

}
