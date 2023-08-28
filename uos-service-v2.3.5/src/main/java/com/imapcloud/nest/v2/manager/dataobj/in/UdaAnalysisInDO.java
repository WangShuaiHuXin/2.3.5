package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 使用uda分析做
 *
 * @author boluo
 * @date 2023-03-06
 */
@ToString
public class UdaAnalysisInDO {

    private UdaAnalysisInDO() {}

    @Data
    public static class AddPictureTaskInDO implements Serializable {

        private String orgCode;

        private List<Long> functionIdList;

        private String pictureUrl;

        /**
         * 业务参数 自定义
         */
        private String businessParam;
    }

    @Data
    public static class CheckPictureTaskInDO implements Serializable {

        private String orgCode;

        private List<Long> functionIdList;

        private Integer pictureNum;
    }
}
