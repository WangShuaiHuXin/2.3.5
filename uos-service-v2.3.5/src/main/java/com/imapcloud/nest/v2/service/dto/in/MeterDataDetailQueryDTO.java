package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 表计数据查询请求信息
 * @author Vastfy
 * @date 2022/12/04 15:59
 * @since 2.1.5
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MeterDataDetailQueryDTO extends PageInfo {

    private String dataId;

    private Integer deviceState;

    private Integer readingState;

    private Integer verificationStatus;

}
