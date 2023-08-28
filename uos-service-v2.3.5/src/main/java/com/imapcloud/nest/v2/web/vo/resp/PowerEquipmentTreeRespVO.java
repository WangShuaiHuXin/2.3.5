package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;
import org.apache.xpath.operations.Bool;

import java.io.Serializable;
import java.util.Map;

@Data
public class PowerEquipmentTreeRespVO implements Serializable {
    private String equipmentId;
    private String spacingUnit;
    private String stationName;
    private String voltageLevel;
    private String equipmentType;
    private String equipmentName;
    private boolean voltageFlag;
    private boolean spacingFlag;
    private boolean typeFlag;
}
