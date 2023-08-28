package com.imapcloud.nest.v2.manager.dataobj.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Builder;
import lombok.Data;

@Data
public class PowerEquipmentQueryDO  extends PageInfo {

    private String equipmentId;

    private String equipmentName;

    private String equipmentType;

    private String pmsId;

    private String spacingName;

    private String voltageLevel;

    private String substation;

    private String orgCode;

    private String beginTime;

    private String endTime;

    private Integer offset;
}
