package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.InspectionQueryPageInDTO;
import com.imapcloud.nest.v2.service.dto.in.InspectionVerificationBatchInDTO;
import com.imapcloud.nest.v2.service.dto.out.InspectionQueryPageOutDTO;
import com.imapcloud.nest.v2.web.vo.req.InspectionQueryPageReqVO;

import java.util.List;

public interface PowerInspectionService {
    boolean verificationBatch(InspectionVerificationBatchInDTO batchIds);

    boolean withdrawalBatch(List<String> batchIds);

    InspectionQueryPageOutDTO inspectionQueryPage(InspectionQueryPageInDTO inDTO);


    boolean inspectionDeleteBatch(List<String> ids);

    boolean infraredWithdrawalBatch(List<String> ids);

    boolean defectWithdrawalBatch(List<String> ids);
}
