package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
public class InspectionVerificationBatchReqVO implements Serializable {

    @NotEmpty(message = "核实状态不能为空")
    private Integer verificationStatus;

    private List<String> ids;
    @NotEmpty(message = "分析类型不能为空")
    private Integer analysisType;
}
