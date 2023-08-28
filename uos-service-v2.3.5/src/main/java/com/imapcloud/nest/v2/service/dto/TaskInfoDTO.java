package com.imapcloud.nest.v2.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 任务信息
 *
 * @author boluo
 * @date 2023-03-16
 */
@Data
public class TaskInfoDTO implements Serializable {

    private String dataId;

    private List<DetailInfoDTO> detailInfoDTOList;

    private Set<String> accountIdSet;

    /**
     * 是否是系统自动AI识别
     */
    private Boolean system;

    @Data
    public static class DetailInfoDTO implements Serializable {

        private String detailId;

        /**
         * 识别功能
         */
        private List<String> functionIdList;

        /**
         * 单位code
         */
        private String orgCode;
    }
}
