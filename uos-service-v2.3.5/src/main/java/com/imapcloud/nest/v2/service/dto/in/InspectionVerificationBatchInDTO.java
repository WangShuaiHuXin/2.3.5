package com.imapcloud.nest.v2.service.dto.in;

import lombok.Builder;
import lombok.Data;
import scala.Int;

import java.util.List;

@Data
@Builder
public class InspectionVerificationBatchInDTO {

    private Integer verificationStatus;
    private String analysisType;

    private List<String> ids;
}
