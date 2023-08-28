package com.imapcloud.nest.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName NestOrgVO.java
 * @Description NestOrgVO
 * @createTime 2022年05月13日 18:09:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class NestOrgVO {

    private static final long serialVersionUID=1L;

    /**
     * 机巢id
     */
    private Integer nestId;

    /**
     * 单位id
     */
    private String name;
}
