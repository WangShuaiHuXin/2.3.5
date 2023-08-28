package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

/**
 * @Classname SetGridManageInDTO
 * @Description 设置网格 dto
 * @Date 2023/4/23 16:21
 * @Author Carnival
 */
@Data
public class GridManageInDTO {

    private String gridManageId;

    private String gridBounds;

    private String orgCode;

    private Integer maxLine;

    private Integer maxCol;

    private Integer taskId;
}
