package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("work_order_plan")
public class WorkOrderPlanEntity  implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer planId;

    private String orderId;

    private Boolean deleted;
}
