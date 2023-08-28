package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
public class BatteryOutDTO {

    private Integer which;

    private List<BattyUseNumsOutDTO> battyUseNumsRespVOList;

    @Data
    public static class BattyUseNumsOutDTO{
        private String batteryNum;
        private Integer charges;
    }


}
