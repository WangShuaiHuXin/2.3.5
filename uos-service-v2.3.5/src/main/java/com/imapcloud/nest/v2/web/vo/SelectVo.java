package com.imapcloud.nest.v2.web.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * select选择器
 *
 * @author boluo
 * @date 2022-07-14
 */
@Data
public class SelectVo implements Serializable {

    /**
     * id
     */
    private String value;

    /**
     * name
     */
    private String label;

    /**
     * 来源
     * -1：系统默认
     * 0：单位自定义
     */
    private Integer source;

}
