package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author double
 * @since 2022-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("flight_mission")
@Accessors(chain = true)
public class FlightMissionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 基站名称
     */
    @TableField(exist = false)
    private String nestName;

    /**
     * 任务执行id
     */
    private String execMissionId;

    /**
     * 基站id
     */
    @Deprecated
    private Integer nestId;


    private String baseNestId;

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
     * 无人机序号
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
