package com.imapcloud.nest.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BuildKmzMissionRes {

    private Boolean flag = false;
    private KzmMission kzmMission;

    @Data
    public static class KzmMission {
        private String missionID;

        private String name;

        private String kmzUrl;

        private String kmzMD5;
    }
}
