package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
public class BatteryRespVO {

    private Integer which;

    private List<BattyUseNumsRespVO> battyUseNumsRespVOList;

    @Data
    public static class BattyUseNumsRespVO{
        private String batteryNum;
        private Integer charges;
    }


}
