package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.DataEquipmentPointInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataEquipmentPointQueryInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataEquipmentPointListOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataEquipmentPointQueryOutDTO;

import java.util.List;

public interface DataEquipmentPointService {
    boolean addEquipmentPoint(DataEquipmentPointInDTO dto);

    boolean editEquipmentPoint(DataEquipmentPointInDTO dto);

    boolean deleteBatchEquipmentPoint(List<String> deletes);

    DataEquipmentPointQueryOutDTO queryPageEquipmentPoint(DataEquipmentPointQueryInDTO inDto);

    List<DataEquipmentPointListOutDTO> queryEquipPointList(String orgCode);
}
