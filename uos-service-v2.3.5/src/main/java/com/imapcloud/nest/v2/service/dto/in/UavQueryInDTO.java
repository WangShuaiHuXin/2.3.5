package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 无人机查询条件
 * @author Vastfy
 * @date 2023/04/19 17:35
 * @since 2.3.2
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UavQueryInDTO extends PageInfo implements Serializable {

    /**
     * 基站监控显示状态
     */
    private Integer showStatus;

}
