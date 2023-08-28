package com.imapcloud.nest.v2.common.enums;

import lombok.Data;

public enum AnalysisResultEnum {


    RESULT_TRUE(1),

    RESULT_FALSE(0);
    private Integer RESULT_CODE;

    AnalysisResultEnum(Integer RESULTC_ODE) {
        this.RESULT_CODE = RESULTC_ODE;
    }

    public Integer getRESULTC_ODE() {
        return RESULT_CODE;
    }

    public void setRESULTC_ODE(Integer RESULTC_ODE) {
        this.RESULT_CODE = RESULTC_ODE;
    }
}
