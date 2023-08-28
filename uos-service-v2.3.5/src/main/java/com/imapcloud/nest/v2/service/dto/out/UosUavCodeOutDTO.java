package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

@Data
public class UosUavCodeOutDTO {
    @Data
    public class UavCameraParamControlOutDTO{

        private String nestId;

        private Integer which;

        private Integer mold;

    }
}
