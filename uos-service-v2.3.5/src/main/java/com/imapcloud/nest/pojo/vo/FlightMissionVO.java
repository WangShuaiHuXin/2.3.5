package com.imapcloud.nest.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName FlightMissionVO.java
 * @Description FlightMissionVO
 * @createTime 2022年03月24日 10:53:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightMissionVO {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 任务执行id
     */
    private String execMissionId;

    /**
     * 任务id
     */
    private String missionId;

    /**
     * 任务名称
     */
    private String missionName;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 里程
     */
    private Double missionDistance;

    /**
     * 时长
     */
    private Double missionDate;

    /**
     * 无人机标识
     */
    private Integer uavWhich;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 删除标识
     */
    private Boolean deleted;

}
