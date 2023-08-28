package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

/**
 * 钉钉
 *
 * @author boluo
 * @date 2022-11-10
 */
public class DingTalkInDO {

    public static final String MARKDOWN = "markdown";

    @Data
    public static class SendInDO {

        private String msgType;

        private String title;

        private String text;

        private Boolean at;
    }
}
