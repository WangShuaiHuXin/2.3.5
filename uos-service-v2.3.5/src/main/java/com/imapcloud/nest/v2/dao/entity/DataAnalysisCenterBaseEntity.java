package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName dataAnalysisCenterBaseEntity.java
 * @Description dataAnalysisCenterBaseEntity
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_analysis_center_base")
public class DataAnalysisCenterBaseEntity extends GenericEntity {

    private Long centerBaseId;

    private String baseName;

    private Long tagId;

    private String tagName;

    private Long taskId;

    private String taskName;

    private Long missionId;

    private Long missionRecordId;

    @Deprecated
    private Long nestId;

    /**
     * 基站ID
     */
    private String baseNestId;

    /**
     * @deprecated 2.0.0，使用orgCode替代
     */
    @Deprecated
    private Long orgId;

    /**
     * 单位编码
     */
    private String orgCode;

    private String nestName;

    private Integer taskType;

    private Integer missionSeqId;

    private LocalDateTime missionRecordTime;

    public Boolean subType;

    private String missionFlyIndex;

}
