package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * UDA AI分析任务响应信息
 * @author Vastfy
 * @date 2022/11/2 16:00
 * @since 2.1.4
 */
@Data
public class UdaAlTaskResponseOutDO implements Serializable {

    /**
     * AI识别任务ID
     */
    private String taskId;

    /**
     * 授权失败列表
     */
    private List<AuthError> authErrorList;

    @Data
    public static class AuthError implements Serializable {

        /**
         * 识别功能ID
         */
        private String functionId;

        /**
         * 识别功能名称
         */
        private String functionName;

        /**
         * 识别功能授权开始日期
         */
        private String authBeginDate;

        /**
         * 识别功能授权结束日期
         */
        private String authEndDate;

        /**
         * 允许调用次数
         */
        private String callCapacity;

        /**
         * 已调用次数
         */
        private String callTimes;

        /**
         * 授权失效原因
         */
        private String authErrDesc;
    }

}
