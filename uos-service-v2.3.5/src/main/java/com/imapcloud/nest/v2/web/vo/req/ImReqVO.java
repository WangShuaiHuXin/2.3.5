package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 即时通信
 *
 * @author boluo
 * @date 2023-02-13
 */
@ToString
public class ImReqVO {

    private ImReqVO() {

    }

    @Data
    public static class CallbackReqVO implements Serializable {

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
         * 类型 0：登录  1：登出  3：清空登录状态
         */
        private int type;
    }

    @Data
    public static class PageReqVO implements Serializable {

        /**
         * 通道标识
         */
        @NotBlank(message = "channelId is blank")
        private String channelId;

        /**
         * 页面
         */
        @NotBlank(message = "page is blank")
        private String page;

        /**
         * 基站uuid
         */
        private String nestUuid;
    }
}
