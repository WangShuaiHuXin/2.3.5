package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.NestCodeOperationInDTO;
import com.imapcloud.nest.v2.service.dto.out.NestCodeOperationOutDTO;

import java.util.List;

public interface NestCodeOperationRecordsService {

    boolean batchSaveRecords(List<NestCodeOperationInDTO> dtoList);

    PageResultInfo<NestCodeOperationOutDTO> listRecordsPage(String nestId,Integer currPage,Integer pageSize);
}
