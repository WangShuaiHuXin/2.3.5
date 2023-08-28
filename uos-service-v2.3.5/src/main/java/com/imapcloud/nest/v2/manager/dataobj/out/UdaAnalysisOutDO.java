package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 调用uda返回结果
 *
 * @author boluo
 * @date 2023-03-14
 */
@ToString
public class UdaAnalysisOutDO {

    private UdaAnalysisOutDO() {}

    @Data
    public static class CheckPictureTaskOutDO implements Serializable {
        private Integer status;

        private List<ErrorInfoOutDO> errorInfoList;
    }

    @Data
    public static class ErrorInfoOutDO implements Serializable {

        private Long functionId;

        private String functionName;

        private String authBeginDate;

        private String authEndDate;

        private Long callCapacity;

        private Long callTimes;

        private String authErrDesc;

        private String authCycle;
    }
}
