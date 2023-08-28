package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("work_order_report")
public class WorkOrderReportEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * 任务工单id
     */
    private String orderId;

    /**
     * 报告id
     */
    private String reportId;
    /**
     * 巡检报告名称
     */
    private String reportName;

    /**
     * minio地址
     */
    private String path;

    /**
     * 是否已删除【0 否 1 是】
     */
    private Boolean deleted;

    /**
     * created_time
     */
    private LocalDateTime createdTime;

    /**
     * creator_id
     */
    private String creatorId;

    /**
     * modifier_id
     */
    private String modifierId;

    /**
     * modified_time
     */
    private LocalDateTime modifiedTime;

    public WorkOrderReportEntity() {
    }
}
