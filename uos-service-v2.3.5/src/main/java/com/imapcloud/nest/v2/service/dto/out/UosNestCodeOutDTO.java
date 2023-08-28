package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
public class UosNestCodeOutDTO {

    @Data
    @Accessors(chain = true)
    public static class NestNetworkStateOutDTO{

        private String nestId;

        private Integer which;

        private Float height;

        private Boolean confirm;

    }

}
