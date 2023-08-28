package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

/**
 * @author wmin
 */
@Data
public class SwitchSdrWorkModeDO {
    private Integer linkWorkmode;

    public SwitchSdrWorkModeDO(Integer linkWorkmode) {
        this.linkWorkmode = linkWorkmode;
    }

    public SwitchSdrWorkModeDO() {
    }

    public enum ModeEnum {
        /**
         * 仅使用sdr
         */
        SDR,
        /**
         * 4G增强模式
         */
        G4;
    }
}
