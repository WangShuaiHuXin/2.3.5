package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 图层查询条件
 * @author Vastfy
 * @date 2022/9/23 17:27
 * @since 2.1.0
 */
@Data
public class MaplayerQueryDTO implements Serializable {

    /**
     * 图层名称【支持模糊检索】
     */
    private String name;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 图层类型
     */
    private Integer type;

}
