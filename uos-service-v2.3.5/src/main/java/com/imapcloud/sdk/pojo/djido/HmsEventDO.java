package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

import java.util.List;

/**
 * @author wmin
 */
@Data
public class HmsEventDO {

    private List<Item> list;

    @Data
    public static class Item {
        private Integer level;
        private Integer module;
        private Integer inTheSky;
        private String code;
        private Integer imminent;
        private String domainType;
        private Args args;
    }

    @Data
    public static class Args {
        private Integer componentIndex;
        private Integer sensorIndex;
        private Integer alarmid;
    }
}
