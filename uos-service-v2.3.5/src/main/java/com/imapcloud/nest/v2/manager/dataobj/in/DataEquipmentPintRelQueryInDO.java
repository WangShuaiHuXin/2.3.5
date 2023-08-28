package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataEquipmentPintRelQueryInDO {

    private String orgCode;

    private String pointId;

    private String equipmentId;
}
