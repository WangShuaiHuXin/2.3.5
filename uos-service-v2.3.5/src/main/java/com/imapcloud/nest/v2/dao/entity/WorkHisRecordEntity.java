package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@TableName("work_his_record")
public class WorkHisRecordEntity extends GenericEntity {
    private String orderId;
    private String recordId;
    private Boolean flag;
    private String description;
    private String mark;
    private Integer orderStatus;
    private Integer processCode;
    private Boolean processDir;
}
