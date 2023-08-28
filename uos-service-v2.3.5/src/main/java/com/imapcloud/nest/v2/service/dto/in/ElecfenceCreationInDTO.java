package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 电子围栏新建信息
 * @author Vastfy
 * @date 2022/9/26 16:27
 * @since 2.1.0
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ElecfenceCreationInDTO extends ElecfenceModificationInDTO {

    /**
     * 电子围栏状态【1：开启；2：关闭】
     */
    private Integer type;

    /**
     * 电子围栏所属单位编码
     */
    private String orgCode;

}
