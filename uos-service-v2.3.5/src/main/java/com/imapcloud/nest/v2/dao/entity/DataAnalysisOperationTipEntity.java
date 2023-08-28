package com.imapcloud.nest.v2.dao.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataAnalysisOperationTipEntity.java
 * @Description DataAnalysisOperationTipEntity
 * @createTime 2022年07月26日 19:51:00
 */
@Data
@Accessors(chain = true)
public class DataAnalysisOperationTipEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Integer id;

    /**
     * 是否提示，1-提示，0-不提示
     */
    private int tip;

    /**
     * 用户id
     */
    private Long creatorId;

    /**
     * create_time
     */
    private LocalDateTime createdTime;

    /**
     * modify_time
     */
    private LocalDateTime modifiedTime;

    /**
     * 是否删除， 0:否，1:是
     */
    private int deleted;

}
