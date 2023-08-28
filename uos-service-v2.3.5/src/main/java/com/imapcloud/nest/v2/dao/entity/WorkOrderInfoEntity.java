package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data

@TableName("work_order_info")
public class WorkOrderInfoEntity extends GenericEntity {

    @TableId(type = IdType.AUTO)
    public Long id;

    private String orderId;

    private  Integer versionId;

    private String title;

    private String orgCode;

    private Integer type;

    private Integer priorityDegree;

    private LocalDateTime insepectionStartTime;

    private LocalDateTime insepectionEndTime;

    private String verificationMethod;

    private String inspectionFrequency;

    private String description;

    private Integer orderStatus;

    private Boolean deleted;
}
