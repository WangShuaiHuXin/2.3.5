package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 自动AI识别任务信息
 * @author Vastfy
 * @date 2022/12/05 15:01
 * @since 2.1.5
 */
@Data
public class AutoAIRecognitionTaskInDTO implements Serializable {

    /**
     * 基础数据ID
     */
    private String baseDataId;

    /**
     * AI分析任务ID，按照前端规则拼接而成
     */
    private String aiTaskName;

    /**
     * AI分析任务类型
     */
    private Integer aiTaskType;

    /**
     * 飞行任务ID
     */
    private String flightTaskId;

    /**
     * 飞行任务标签信息[名称]
     */
    private String flightTaskTag;

    /**
     * 飞行任务名称（冗余）
     */
    private String taskName;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 分析图片信息列表
     */
    private List<PhotoInfo> photoInfos;

    @Data
    public static class PhotoInfo {

        /**
         * 业务数据详情ID
         */
        private String detailId;
        /**
         * 图片名称
         */
        private String photoName;

        /**
         * 图片存储路径
         */
        private String photoPath;

        /**
         * 失败原因
         */
        private String failedReason;

    }

}
