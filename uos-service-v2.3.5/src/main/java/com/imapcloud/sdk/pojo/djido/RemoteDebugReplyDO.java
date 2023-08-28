package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

@Data
public class RemoteDebugReplyDO {
    private Integer result;
    private Output output;

    @Data
    public static class Output {
        private String status;
    }

    public enum StatusEnum {
        SENT("sent", "已下发"),
        IN_PROGRESS("in_progress", "执行中"),
        OK("ok", "执行成功"),
        PAUSED("paused", "暂停"),
        REJECTED("rejected", "拒绝"),
        FAILED("failed", "失败"),
        CANCELED("canceled", "取消或终止"),
        TIMEOUT("timeout", "超时"),
        UNKNOWN("unknown", "未知");
        private String code;
        private String express;

        StatusEnum(String code, String express) {
            this.code = code;
            this.express = express;
        }

        public static StatusEnum getInstance(String code) {
            for (StatusEnum e : StatusEnum.values()) {
                if (e.code.equals(code)) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }
}
