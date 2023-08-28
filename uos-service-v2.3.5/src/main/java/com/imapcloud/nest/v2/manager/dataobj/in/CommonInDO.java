package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.sdk.manager.DjiTslSnParam;
import lombok.Data;
import lombok.ToString;

/**
 * CommonNestStateFactory ComponentManagerFactory 管理
 *
 * @author boluo
 * @date 2022-09-02
 */
@ToString
public class CommonInDO {
    private CommonInDO() {}

    @Data
    public static class MqttOptionsInDO {

        private String nestUuid;

        private String username;
        private String password;
        private String serverUri;

        private int nestType;

        private DjiTslSnParam djiTslSnParam;
    }
}
