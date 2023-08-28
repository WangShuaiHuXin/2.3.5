package com.imapcloud.nest.pojo.vo.resp;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务信息
 *
 * @author boluo
 * @date 2022-09-29
 */
@Data
public class TaskRespVO implements Serializable {

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
    @Deprecated
    private Integer nestId;

    private String baseNestId;

    /**
     * 该条记录被复制的次数
     */
    private Integer copyCount;

    /**
     * 航线种类，0 - 机巢航线，1 - 易飞终端航线
     */
    private Integer mold;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 变电站规划的次级类别0是本地任务，1是动态任务
     */
    private Integer subType;

    private Long creatorId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

    private Integer dataType;

    private Integer tagId;

    private String orgName;

    private String baseNestName;
}
