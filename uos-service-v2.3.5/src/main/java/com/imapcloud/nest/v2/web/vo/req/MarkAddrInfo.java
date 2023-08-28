package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @deprecated 2.2.3，使用新接口{@link com.imapcloud.nest.v2.web.vo.req.MarkAddrInfoReqVO}替代，将在后续版本删除
 */
@Deprecated
@Data
public class MarkAddrInfo {

    @NotNull(message = "markId 不能为空")
    private String markId;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String addr;

}
