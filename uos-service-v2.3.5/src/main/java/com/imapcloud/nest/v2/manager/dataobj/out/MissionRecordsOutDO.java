package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 架次记录
 *
 * @author boluo
 * @date 2022-11-30
 */
@Data
public class MissionRecordsOutDO {

    private Long missionRecordsId;

    private Integer missionId;

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
     * 架次飞行第几次
     */
    private Integer flyIndex;
}
