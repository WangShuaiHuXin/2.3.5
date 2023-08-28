package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CpsCameraReqVO implements Serializable {
    @Data
    public static class CpsCameraStatusReqVO {
        private List<Integer> formats;
    }

    @Data
    public static class CpsCameraZoomReqVO {
        private Integer source;
    }
}
