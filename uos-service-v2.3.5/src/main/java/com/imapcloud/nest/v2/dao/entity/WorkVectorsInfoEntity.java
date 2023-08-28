package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;

@Data
@TableName("work_vectors_info")
public class WorkVectorsInfoEntity extends GenericEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderId;
    private String vectorId;
    private String name;
    private Integer type;
    private String points;
    private Integer sequence;
}
