package com.imapcloud.nest.pojo.dto;

import lombok.Data;

/**
 * Created by wmin on 2020/12/16 10:05
 *
 * @author wmin
 */
@Data
public class ListTaskRequestParamDto {
    /**
     * 基槽Id
     */
    private String nestId;
    /**
     * 当前页
     */
    private Integer currPage;
    /**
     * 每页条数
     */
    private Integer pageSize;
    /**
     * 关键词
     */
    private String keyword;
    /**
     * 标签id
     */
    private Integer tagId;
    /**
     * 任务类型
     */
    private Integer taskType;
    /**
     * 任务种类，0-基站，1-易飞终端
     */
    private Integer mold;
    /**
     * 单位Id
     */
    private String unitId;
}
