package com.imapcloud.nest.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DjiStartTaskParamDTO {

    /**
     * 任务id
     */
    private Integer taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 架次名称
     */
    private String missionName;

    /**
     * 返航高度，起降高度
     */
    private Integer rthAltitude;

    /**
     * 新基站表id
     */
    private String baseNestId;

    /**
     * 任务类型
     */
    private Integer taskType;
}
