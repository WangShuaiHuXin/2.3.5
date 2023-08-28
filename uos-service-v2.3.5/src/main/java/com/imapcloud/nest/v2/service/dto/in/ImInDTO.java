package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;
import lombok.ToString;

/**
 * im
 *
 * @author boluo
 * @date 2023-02-13
 */
@ToString
public class ImInDTO {

    private ImInDTO() {

    }

    @Data
    public static class CallbackInDTO {

        /**
         * 帐户id
         */
        private String accountId;

        /**
         * 通道标识
         */
        private String channelId;

        /**
         * 语言
         */
        private String language;

        /**
         * 类型 0：登录  1：登出  2：清空登录状态
         */
        private int type;
    }

    @Data
    public static class PageInDTO {

        private String orgCode;

        /**
         * 帐户id
         */
        private String accountId;

        /**
         * 通道标识
         */
        private String channelId;

        /**
         * 页面
         */
        private String page;

        /**
         * 基站uuid
         */
        private String nestUuid;
    }
}
