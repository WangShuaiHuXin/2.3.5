package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

@Data
public class RemoteDebugProgressDO {

    private Integer result;

    private Output output;

    @Data
    public static class Output {

        /**
         * 任务状态
         */
        private String status;

        /**
         * 进度
         */
        private Progress progress;
    }

    @Data
    public static class Progress {

        /**
         * 进度百分比
         */
        private Integer percent;

        /**
         * 当前步骤
         */
        private String stepKey;

        /**
         * 步骤结果，非0代表错误
         */
        private Integer stepResult;
    }


}
