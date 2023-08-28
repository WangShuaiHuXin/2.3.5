package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据分析-基础数据简要信息
 * @author Vastfy
 * @date 2022/11/3 09:00
 * @since 2.1.4
 */
@Data
public class DataAnalysisBaseSimpleOutDTO implements Serializable {

    private Long centerBaseId;

    private String name;

    private Long tagId;

    private String tagName;

    private Long taskId;

    private String taskName;

}
