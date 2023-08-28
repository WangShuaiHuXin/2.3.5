package com.imapcloud.nest.v2.dao.po.out;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderReportOutPO {

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
    private boolean deleted;

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

}
