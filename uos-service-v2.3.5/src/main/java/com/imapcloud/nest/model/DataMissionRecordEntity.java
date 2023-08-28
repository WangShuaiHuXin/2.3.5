package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 点云、正射关联架次记录
 * @author zheng
 * @since 2021-06-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_mission_record")
public class DataMissionRecordEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 点云关联架次记录表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 原数据id（点云，正射）
     */
    private Integer dataId;

    /**
     * 架次记录id
     */
    private Integer missionRecordsId;

    /**
     * 数据类型
     */
    private Integer dataType;

    private Integer taskId;
    private String taskName;
    private LocalDateTime missionRecordTime;

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


}
