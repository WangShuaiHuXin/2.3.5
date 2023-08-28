package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

import java.util.List;

@Data
public class DjiDockLiveStateDO {

    private List<LiveSta> liveStatus;

    @Data
    public static class LiveSta {
        private String videoId;
        private Integer videoQuality;
        private Integer status;
        private String videoType;
        private Integer error_status;
    }

}
