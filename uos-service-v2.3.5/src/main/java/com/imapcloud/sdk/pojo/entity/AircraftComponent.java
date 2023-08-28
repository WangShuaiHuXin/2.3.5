package com.imapcloud.sdk.pojo.entity;

import com.imapcloud.sdk.pojo.constant.AircraftComponentNameEnum;

public class AircraftComponent {
    private AircraftComponentNameEnum aircraftComponentName = AircraftComponentNameEnum.UNKNOWN;
    private String aircraftFirmware = "";
    private String aircraftSerialNumber = "";
    private Integer baseObjId = 0;

    public AircraftComponentNameEnum getAircraftComponentName() {
        return aircraftComponentName;
    }

    public void setAircraftComponentName(AircraftComponentNameEnum aircraftComponentName) {
        if (aircraftComponentName != null) {
            this.aircraftComponentName = aircraftComponentName;
        }
    }

    public String getAircraftSerialNumber() {
        return aircraftSerialNumber;
    }

    public String getAircraftFirmware() {
        return aircraftFirmware;
    }

    public void setAircraftFirmware(String aircraftFirmware) {
        if (aircraftFirmware != null) {
            this.aircraftFirmware = aircraftFirmware;
        }
    }

    public void setAircraftSerialNumber(String aircraftSerialNumber) {
        if (aircraftSerialNumber != null) {
            this.aircraftSerialNumber = aircraftSerialNumber;
        }
    }

    public Integer getBaseObjId() {
        return baseObjId;
    }

    public void setBaseObjId(Integer baseObjId) {
        if (baseObjId != null) {
            this.baseObjId = baseObjId;
        }
    }

}
