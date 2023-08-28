package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
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
@TableName("data_analysis_center_detail")
public class DataAnalysisCenterDetailEntity extends GenericEntity {

    private Long centerBaseId;

    private Long centerDetailId;

    private Long photoId;

    private String photoName;

    private Integer photoState;

    private Integer pushState;

    private Long tagId;

    private Long taskId;

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

    private String thumImageMarkPath;

    private String imageMarkPath;

    private String thumImagePath;

    private String imagePath;

    private Integer srcDataType;

    private Integer picType;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Integer originalWidth;

    private Integer originalHeight;

    private LocalDateTime photoCreateTime;

}
