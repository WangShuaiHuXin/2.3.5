package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("nest_code_operation_records")
public class NestCodeOperationRecordsEntity {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 基站id
     */
    private String nestId;

    /**
     * 基站指令
     */
    private String nestCode;

    /**
     * 用户id
     */
    private String creatorId;

    /**
     * 基站日志路径
     */
    private LocalDateTime creatorTime;
}
