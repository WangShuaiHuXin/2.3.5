package com.imapcloud.nest.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 任务表
 * </p>
 * @since 2020-08-27
 */
@Data
public class TaskAndMissionRecordsDto{

    private Integer id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 描述，巡检作业周期
     */
    private String description;

    /**
     * 任务类型Id
     */
    private Integer type;

    /**
     * 机巢id，只有机巢航线才有（mold=0）
     */
    private Integer nestId;

    /**
     * 该条记录被复制的次数
     */
    private Integer copyCount;

    /**
     * 航线种类，0 - 机巢航线，1 - 易飞终端航线
     */
    private Integer mold;

    /**
     * 单位Id,只有易飞终端的航线才有(mold=1)
     */
    private Integer unitId;

    /**
     *
     * 变电站的识别类型，0是缺陷识别，1是表计读数，2是红外测温'
     */
    private Integer identificationType;

    /**
     *
     * 变电站规划的次级类别0是本地任务，1是动态任务
     */
    private Integer subType;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

    private String missionName;

    private Integer missionId;

    private LocalDateTime missionEndTime;

    private Integer flyIndex;

    private Integer missionRecordsId;
}
